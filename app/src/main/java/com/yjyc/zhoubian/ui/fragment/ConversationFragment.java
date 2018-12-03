package com.yjyc.zhoubian.ui.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.blankj.utilcode.util.StringUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.orhanobut.hawk.Hawk;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuCreator;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItem;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItemClickListener;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;
import com.yjyc.zhoubian.HttpUrl;
import com.yjyc.zhoubian.R;
import com.yjyc.zhoubian.im.ECMIm;
import com.yjyc.zhoubian.im.chat.adapter.ConversationAdapter;
import com.yjyc.zhoubian.im.entity.Conversation;
import com.yjyc.zhoubian.model.Login;
import com.yjyc.zhoubian.model.LoginModel;
import com.yjyc.zhoubian.model.SiteMsgs;
import com.yjyc.zhoubian.model.SiteMsgsModel;
import com.yjyc.zhoubian.model.UnreadSiteMsgNum;
import com.yjyc.zhoubian.model.UnreadSiteMsgNumModel;
import com.yjyc.zhoubian.model.UserInfo;
import com.yjyc.zhoubian.model.UserInfoModel;
import com.yuqian.mncommonlibrary.http.OkhttpUtils;
import com.yuqian.mncommonlibrary.http.callback.AbsJsonCallBack;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class ConversationFragment extends Fragment {

    @BindView(R.id.conversation_list)
    SwipeMenuRecyclerView conversationList;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;

    private Unbinder unbinder;
    public ConversationAdapter conversationAdapter;

    public ConversationFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_conversation, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        LinearLayoutManager layoutmanager = new LinearLayoutManager(getActivity());
        conversationList.setLayoutManager(layoutmanager);
        conversationList.setSwipeMenuCreator(swipeMenuCreator);
        conversationList.setSwipeMenuItemClickListener(mMenuItemClickListener);

        conversationAdapter = new ConversationAdapter(getActivity(), ECMIm.getInstance().conversations);
        conversationList.setAdapter(conversationAdapter);

        updateConversation();

        refreshLayout.setOnRefreshListener(refreshlayout -> {
            updateConversation();
        });

    }

    private void updateConversation() {
        reqOfficalMsg();
        for (int i = 0; i < ECMIm.getInstance().conversations.size(); i++) {
            if(ECMIm.getInstance().conversations.get(i).getFrom().equals("-1")){
                continue;
            }
            repUserInfo(ECMIm.getInstance().conversations.get(i).getFriend().uid);
        }
        refreshLayout.finishRefresh();
    }

    private void reqOfficalMsg() {
        Login login = Hawk.get("LoginModel");
        if(login == null){
            return;
        }
        OkhttpUtils.with()
                .get()
                .url(HttpUrl.SITEMSG)
                .addParams("uid", login.uid + "")
                .addParams("token", login.token)
                .execute(new AbsJsonCallBack<UnreadSiteMsgNumModel, UnreadSiteMsgNum>() {
                    @Override
                    public void onSuccess(UnreadSiteMsgNum body) {
                        if(body == null){
                            return;
                        }
                        if(body.number > 0){
                            conversationAdapter.updateOfficialMsg(body);
                        }else{
                            ECMIm.getInstance().clearOfficialUndead();
                        }
                        updateHomeUnread();
                    }
                    @Override
                    public void onFailure(String errorCode, String errorMsg) {
                    }
                });
    }

    private void updateHomeUnread() {
        ECMIm.getInstance().updateHomeUnread();
    }

    private void repUserInfo(int uid){
        OkhttpUtils.with()
                .post()
                .url(HttpUrl.USERINFO)
                .addParams("uid", uid + "")
                .execute(new AbsJsonCallBack<UserInfoModel, UserInfo>() {
                    @Override
                    public void onFailure(String errorCode, String errorMsg) {
                    }

                    @Override
                    public void onSuccess(UserInfo body) {
                        setFriendInfo(body);
                    }
                });
    }

    private void setFriendInfo(UserInfo body) {
        for (int i = 0; i < ECMIm.getInstance().conversations.size(); i++) {
            Conversation conversation = ECMIm.getInstance().conversations.get(i);
            if(conversation.getFriend().uid == body.uid){
                conversation.setFriend(body);
                conversationAdapter.notifyItemChanged(i);
                synchronized (ECMIm.getInstance().conversations){
                    Hawk.put("conversations", ECMIm.getInstance().conversations);
                }
                return;
            }
        }
        updateHomeUnread();
    }

    /**
     * 菜单创建器。在Item要创建菜单的时候调用。
     */
    private SwipeMenuCreator swipeMenuCreator = (swipeLeftMenu, swipeRightMenu, viewType) -> {

        int width = getResources().getDimensionPixelSize(R.dimen.dp_65);
        int height = ViewGroup.LayoutParams.MATCH_PARENT;

        SwipeMenuItem setTopItem = new SwipeMenuItem(getActivity())
                .setBackground(R.color.gray)
                .setText("置顶")
                .setTextColor(Color.WHITE)
                .setTextSize(16)
                .setWidth(width)
                .setHeight(height);
        swipeRightMenu.addMenuItem(setTopItem);

        SwipeMenuItem closeItem = new SwipeMenuItem(getContext())
                .setBackground(R.color.red)
                .setText("删除")
                .setTextColor(Color.WHITE)
                .setTextSize(16)
                .setWidth(width)
                .setHeight(height);
        swipeRightMenu.addMenuItem(closeItem);
    };

    /**
     * RecyclerView的Item中的Menu点击监听。
     */
    private SwipeMenuItemClickListener mMenuItemClickListener = menuBridge -> {
        menuBridge.closeMenu();

        int direction = menuBridge.getDirection(); // 左侧还是右侧菜单。
        int adapterPosition = menuBridge.getAdapterPosition(); // RecyclerView的Item的position。
        int menuPosition = menuBridge.getPosition(); // 菜单在RecyclerView的Item中的Position。

        if (direction == SwipeMenuRecyclerView.RIGHT_DIRECTION) {
            //Toast.makeText(getContext(), "list第" + adapterPosition + "; 右侧菜单第" + menuPosition, Toast.LENGTH_SHORT).show();
            if(menuPosition == 0){
                Conversation conversation = ECMIm.getInstance().conversations.get(adapterPosition);
                ECMIm.getInstance().conversations.remove(adapterPosition);
                ECMIm.getInstance().conversations.add(0, conversation);
                conversationAdapter.notifyDataSetChanged();
                Hawk.put("conversations", ECMIm.getInstance().conversations);
            }else if(menuPosition == 1){
                if(ECMIm.getInstance().conversations.get(adapterPosition).getFrom().equals("-1")){
                    return;
                }
                ECMIm.getInstance().conversations.remove(adapterPosition);
                conversationAdapter.notifyDataSetChanged();
                Hawk.put("conversations", ECMIm.getInstance().conversations);
            }
        }
    };

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}

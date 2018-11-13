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

import com.yanzhenjie.recyclerview.swipe.SwipeMenuCreator;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItem;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItemClickListener;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;
import com.yjyc.zhoubian.R;
import com.yjyc.zhoubian.adapter.ConversationAdapter;
import com.yjyc.zhoubian.im.entity.Conversation;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class ConversationFragment extends Fragment {

    @BindView(R.id.conversation_list)
    SwipeMenuRecyclerView conversationList;

    private Unbinder unbinder;
    private List<Conversation> conversations;
    private ConversationAdapter conversationAdapter;

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

        conversationAdapter = new ConversationAdapter(getActivity(), conversations);
        conversationList.setAdapter(conversationAdapter);

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
            Toast.makeText(getContext(), "list第" + adapterPosition + "; 右侧菜单第" + menuPosition, Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}

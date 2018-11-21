package com.yjyc.zhoubian.model;

public class CheckEvaluationExpose {
    public CheckEvaluation check_evaluation;
    public CheckExpose check_expose;
    public class CheckEvaluation{
        public String message;
        public int status;
    }
    public class CheckExpose{
        public String message;
        public int status;
    }
}

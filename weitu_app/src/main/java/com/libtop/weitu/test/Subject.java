package com.libtop.weitu.test;

import com.google.gson.Gson;

import java.io.Serializable;
import java.util.List;

/**
 * Created by zeng on 2016/9/9.
 */
public class Subject implements CategoryResult{

        public int sid;
        public String name;
        public String intro;
        public String cover;
        /**
         * id : 1
         * name : 移动开发
         */

        public CategoryBean category;
        public int uid;
        public int count_follow;
        public int has_new;
        /**
         * uid : 1
         * mobile : 13800000001
         * name : 小华
         * logo : http://v1.qzone.cc/avatar/201403/15/22/11/53245faf8a2ed399.jpg%21200x200.jpg
         * sex : 0
         */

        public UserBean user;

        public static class CategoryBean {
            public int id;
            public String name;

            public static CategoryBean objectFromData(String str) {

                return new Gson().fromJson(str, CategoryBean.class);
            }
        }

        public static class UserBean {
            public int uid;
            public long mobile;
            public String name;
            public String logo;
            public int sex;

            public static UserBean objectFromData(String str) {

                return new Gson().fromJson(str, UserBean.class);
        }
    }
}
package com.BookRoom.entity.view;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("v_user_info")
public class UserView {
    @TableId("user_id")
    private String userId;      // 用户ID
    private String roleName;        // 角色 (admin, customer, employee)

    @TableField(exist = false)
    private String name;
}

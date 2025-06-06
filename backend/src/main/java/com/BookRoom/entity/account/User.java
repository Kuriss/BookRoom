package com.BookRoom.entity.account;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("user")
public class User
{
    @TableId
    private String userId;
    private String encryptedPassword;
    private Byte roleId;
}

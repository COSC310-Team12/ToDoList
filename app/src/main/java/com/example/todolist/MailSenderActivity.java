package com.example.todolist;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


public class MailSenderActivity extends Activity {


    public void onClick(View v){
        try
        {
            //Hard coded for now - in future can use .getText().toString wit user input
            GMailSender sender = new GMailSender("cosc310team12@gmail.com", "Cosc310_Team12!");
            sender.sendMail("You have a late task!", "Warning task 2 is now late!","cosc310team12@gmail.com","Mrgauthier34@gmail.com");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


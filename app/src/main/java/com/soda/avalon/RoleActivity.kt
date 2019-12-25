package com.soda.avalon

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_role.*
import kotlin.random.Random

class RoleActivity : AppCompatActivity() {
    val rolePictuerUriArray : Array<Int> = arrayOf(R.drawable.merlin,R.drawable.percival,R.drawable.good,R.drawable.killer,R.drawable.mormna)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_role)

        currentPlayer = getYourPlayer(currentRoom, nickName)!!

        bindView(currentPlayer!!)

    }

    fun getYourPlayer(currentRoom : Room, nickName: String) :Player?{
        currentRoom.players.forEach {
            if (it.nickName == nickName){
                return it
            }
        }
        return null
    }

    fun bindView(player :Player){
        if (player.role!=null){
            val role = player.role
            imv_rolecard.setImageResource(rolePictuerUriArray[role!!])
            tv_player.text =  "${player.nickName}您本場的角色是"
            tv_role.text = resources.getStringArray(R.array.role_list)[role!!]
            tv_role_Introduction.text = resources.getStringArray(R.array.role_Introduction)[role!!]

            when(player.role){
                0->{
                    tv_skill.text = "您本場為正義方\n本場邪惡方為:${getSkill(player)}"
                }
                1->{
                    tv_skill.text = "您本場為正義方\n本場的梅林可能為:${getSkill(player)}"
                }
                2->{
                    tv_skill.text = "您本場為正義方"
                }
                3->{
                    tv_skill.text = "您本場為反派\n您的隊友是:${getSkill(player)}"
                }
                4->{
                    tv_skill.text = "您本場為反派\n您的隊友是:${getSkill(player)}"
                }

            }
        }

    }

    fun getSkill(player: Player) :String{
        var ans = ""
        when(player.role){
            0->{
                currentRoom.players.forEach {
                    if (it.role!!>= 3){
                        ans+="${it.nickName} "
                    }
                }
                return ans
            }
            1->{
                currentRoom.players.forEach {
                    if (it.role ==0){
                        ans+="${it.nickName} "
                    }else if(it.role == 3){
                        ans+="${it.nickName} "
                    }
                }
                return ans
            }
            3->{
                currentRoom.players.forEach {
                    if (it.role ==4){
                        return it.nickName
                    }
                }

            }
            4->{
                currentRoom.players.forEach {
                    if (it.role ==3){
                        return it.nickName
                    }
                }
            }
        }
        return ""
    }


}

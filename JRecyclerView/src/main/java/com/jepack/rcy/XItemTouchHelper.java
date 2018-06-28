package com.jepack.rcy;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

/**
 * TODO Add class comments here.
 * Created by zhanghaihai on 2018/5/24.
 */
public class XItemTouchHelper {
    public void attachSwipeMoreAction(Context context, RecyclerView recyclerView, @NonNull final XItemMenu menu){

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.Callback() {
            @Override
            public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                return 0;
            }

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                if(direction == ItemTouchHelper.RIGHT ){
                    menu.setState(XItemMenu.MENU_HIDED);
                }else{
                    menu.setState(XItemMenu.MENU_SHOWED);
                }
            }

            @Override
            public void onMoved(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, int fromPos, RecyclerView.ViewHolder target, int toPos, int x, int y) {
                super.onMoved(recyclerView, viewHolder, fromPos, target, toPos, x, y);

            }
        });
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }
}

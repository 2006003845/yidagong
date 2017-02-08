package com.zrlh.llkc.corporate;

import java.util.ArrayList;

import android.app.Activity;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.zrlh.llkc.R;

/*
 * 下拉式菜单列表
 * */
public class PullDownList {

    private PopupWindow selectPopupWindow = null;
    private View parentView = null;
    private Activity activity = null;
    private ListView listView = null;
    private PullDownListAdapter pullListAdapter = null;
    private DeleteData deleteData = null;
    ArrayList<Obj> data = null;

    private int[] location = new int[2];
    private int height;
    private int width;

    private PullOnItemClickListener onItemClickListener;

    public void setPullOnItemClickListener(PullOnItemClickListener onPullItemClickListener) {
        this.onItemClickListener = onPullItemClickListener;
    }

    /**
     * 
     * @param _activity
     *            调用此方法的Activity
     * @param _parentView
     *            用来显示的PopupWindow的View
     * @param _ShowlocationView
     *            基于那个控件显示
     * @param _data
     *            数据源
     */
    public PullDownList(Activity _activity, View _parentView, View _ShowlocationView, ArrayList<Obj> _data, int h) {
        activity = _activity;
        parentView = _parentView;
        // _ShowlocationView.getLocationOnScreen(location);
        parentView.getLocationOnScreen(location);
        width = parentView.getWidth();
        height = _ShowlocationView.getHeight();
        data = _data;
        initPopuWindow(h);
    }

    public PullDownList(Activity _activity, View _parentView, View _ShowlocationView, ArrayList<Obj> _data) {
        activity = _activity;
        parentView = _parentView;
        // _ShowlocationView.getLocationOnScreen(location);
        parentView.getLocationOnScreen(location);
        width = parentView.getWidth();
        height = _ShowlocationView.getHeight();
        data = _data;
        initPopuWindow(-1);
    }

    /**
     * 初始化PopupWindow
     */
    private void initPopuWindow(int height) {
        View loginwindow = (View) activity.getLayoutInflater().inflate(R.layout.popupwindow_pull_down, null);
        listView = (ListView) loginwindow.findViewById(R.id.popupwindow_pulldown_list);
        // LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) listView
        // .getLayoutParams();
        // if (height != -1)
        // lp.height = height;
        // listView.setLayoutParams(lp);
        pullListAdapter = new PullDownListAdapter(activity, data);
        listView.setAdapter(pullListAdapter);
        selectPopupWindow = new PopupWindow(loginwindow, width, height == -1 ? LayoutParams.WRAP_CONTENT : height, true);
        // selectPopupWindow = new PopupWindow(loginwindow, width,
        // LayoutParams.WRAP_CONTENT, true);
        selectPopupWindow.setOutsideTouchable(true);
        selectPopupWindow.setBackgroundDrawable(new BitmapDrawable());
        // popupWindow.setAnimationStyle(R.style.MenuAnimation);
        listView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View arg1, int position, long arg3) {
                // setSelected(data.get(arg2).cont);
                onItemClickListener.onPullItemClick(parent, position);
                selectPopupWindow.dismiss();
            }
        });
    }

    /**
     * 显示PopupWindow窗口
     * 
     * @param popupwindow
     */
    public void popupWindwShowing() {
        selectPopupWindow.showAtLocation(parentView, Gravity.NO_GRAVITY, location[0], location[1] + height);
    }

    /**
     * PopupWindow消失
     */
    public void dismiss() {
        selectPopupWindow.dismiss();
    }

    public void setOnDeleteData(DeleteData _deleteData) {
        deleteData = _deleteData;
    }

    public void setOnSelected(DeleteData _deleteData) {
        deleteData = _deleteData;
    }

    public interface DeleteData {
        public void deletePosition(int position);

        public void selected(String position);
    }

    class PullDownListAdapter extends BaseAdapter {
        private ArrayList<Obj> list;
        private Activity activity = null;

        public PullDownListAdapter(Activity _activity, ArrayList<Obj> _list) {
            activity = _activity;
            list = _list;
        }

        public int getCount() {
            return list.size();
        }

        public Object getItem(int position) {
            return list.get(position);
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = LayoutInflater.from(activity).inflate(R.layout.pull_down_list_item, null);
                holder.textView = (TextView) convertView.findViewById(R.id.pull_list_item_text);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            String title = list.get(position).name;
            holder.textView.setText(title == null ? "(空)" : title);
            return convertView;
        }

        class ViewHolder {
            TextView textView;

        }
        //
        // private void removeItem(int position) {
        // list.remove(position);
        // notifyDataSetChanged();
        // }

    }

    public void clear() {
        try {
            listView.removeAllViews();
        } catch (Exception e) {
            // TODO: handle exception
        }

    }
}

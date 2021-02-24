package vip.ablog.vientiane.utils;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import vip.ablog.vientiane.app.App;

public class ToastUtil {
   private static Toast toast;
   private  static Context context = App.getContext();
   private static View view;

   private ToastUtil() {
   }

   private static void getToast(Context context) {
      if (toast == null) {
         toast = new Toast(context);
      }
      if (view == null) {
         view = Toast.makeText(context, "", Toast.LENGTH_SHORT).getView();
      }
      toast.setView(view);
   }

   public static void showShortToast( CharSequence msg) {
      showToast(context, msg, Toast.LENGTH_SHORT);
   }

   public static void showShortToast(Context context, int resId) {
      showToast(context, resId, Toast.LENGTH_SHORT);
   }

   public static void showLongToast( CharSequence msg) {
      showToast(context, msg, Toast.LENGTH_SHORT);
   }
   public static void showLongToast(Context context, CharSequence msg) {
      showToast(context, msg, Toast.LENGTH_SHORT);
   }
   public static void showShortToast(Context context, CharSequence msg) {
      showToast(context, msg, Toast.LENGTH_SHORT);
   }

   public static void showLongToast(int resId) {
      showToast(context, resId, Toast.LENGTH_LONG);
   }

   private static void showToast(Context context, CharSequence msg, int duration) {
      try {
         getToast(context);
         toast.setText(msg);
         toast.setDuration(duration);
         toast.setGravity(Gravity.CENTER, 0, 0);
         toast.show();
      } catch (Exception e) {

      }
   }

   private static void showToast(Context context, int resId, int duration) {
      try {
         if (resId == 0) {
            return;
         }
         getToast(context);
         toast.setText(resId);
         toast.setDuration(duration);
         toast.setGravity(Gravity.CENTER, 0, 0);
         toast.show();
      } catch (Exception e) {

      }
   }

   public static void cancelToast() {
      if (toast != null) {
         toast.cancel();
      }
   }

}
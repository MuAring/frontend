//package org.maru.muaring.feature.history.ui.decoration;
//
//import android.content.Context;
//import android.graphics.Canvas;
//import android.graphics.Paint;
//import android.view.View;
//
//import androidx.annotation.NonNull;
//import androidx.core.content.ContextCompat;
//import androidx.recyclerview.widget.RecyclerView;
//
//import org.maru.muaring.feature.R;
//
//public class MusicHistoryTimelineDecoration extends RecyclerView.ItemDecoration {
//
//    private final Paint paint;
//
//    public MusicHistoryTimelineDecoration(Context context) {
//        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
//        // 색은 배지 색과 맞추기 (#72A775)
//        int color = ContextCompat.getColor(context, org.maru.muaring.design.R.color.green); // 없으면 hex로 바꿔도 됨
//        paint.setColor(color);
//        paint.setStrokeWidth(dpToPx(context, 2f));
//        paint.setStyle(Paint.Style.STROKE);
//    }
//
//    @Override
//    public void onDrawOver(@NonNull Canvas canvas,
//                           @NonNull RecyclerView parent,
//                           @NonNull RecyclerView.State state) {
//        super.onDrawOver(canvas, parent, state);
//
//        int childCount = parent.getChildCount();
//        if (childCount <= 1) return;
//
//        for (int i = 0; i < childCount - 1; i++) {
//            View child = parent.getChildAt(i);
//            View nextChild = parent.getChildAt(i + 1);
//
//            View badge = child.findViewById(R.id.text_badge_day);
//            View nextBadge = nextChild.findViewById(R.id.text_badge_day);
//
//            if (badge == null || nextBadge == null) continue;
//
//            // RecyclerView 좌표계 기준 X, Y 계산
//            float badgeCenterX = child.getLeft() + badge.getLeft() + (badge.getWidth() / 2f);
//
//            float startY = child.getTop() + badge.getBottom();
//            float endY = nextChild.getTop() + nextBadge.getTop();
//
//            canvas.drawLine(badgeCenterX, startY, badgeCenterX, endY, paint);
//        }
//    }
//
//    private float dpToPx(Context context, float dp) {
//        return dp * context.getResources().getDisplayMetrics().density;
//    }
//}

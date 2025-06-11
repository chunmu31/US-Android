package com.bcm.us_project.calen;

import android.graphics.drawable.Drawable;

import com.bcm.us_project.CalendarFragment;
import com.bcm.us_project.R;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.spans.DotSpan;

import java.util.Collection;
import java.util.HashSet;

import de.hdodenhof.circleimageview.CircleImageView;

// 일정이 있을 시 표현해주는 부분
public class EventDecorator implements DayViewDecorator {

    private final Drawable drawable;
    private int color;
    private HashSet<CalendarDay> dates;

    public EventDecorator(int color, Collection<CalendarDay> dates, CalendarFragment context) {
        drawable = context.getResources().getDrawable(R.drawable.more2);
        this.color = color;
        this.dates = new HashSet<>(dates);
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return dates.contains(day);
    }

    @Override
    public void decorate(DayViewFacade view) {
        view.setSelectionDrawable(drawable);
        view.addSpan(new DotSpan(15, color)); // 날자밑에 점
    }



}

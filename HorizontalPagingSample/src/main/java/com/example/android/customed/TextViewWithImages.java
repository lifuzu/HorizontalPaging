package com.example.android.customed;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.Spannable;
import android.text.style.ImageSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;

import com.example.android.horizontalpaging.R;

/**
 * Created by Richard Lee on 5/5/14.
 */
public class TextViewWithImages extends TextView {

    public TextViewWithImages(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
    public TextViewWithImages(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public TextViewWithImages(Context context) {
        super(context);
    }
    @Override
    public void setText(CharSequence text, BufferType type) {
        Spannable s = getTextWithImages(getContext(), text);
        super.setText(s, BufferType.SPANNABLE);
    }

    private static final Spannable.Factory spannableFactory = Spannable.Factory.getInstance();

    private static boolean addImages(Context context, Spannable spannable) {
        //final String IMAGE_PATTERN = "([^\\w]+\\.(?i)(bmp|jpg|gif|png)$)";
        final String IMAGE_PATTERN = "(\\w.*)";
        Pattern refImg = Pattern.compile("\\Q[img src=\\E" + IMAGE_PATTERN + "\\Q/]\\E");
        boolean hasChanges = false;

        Matcher matcher = refImg.matcher(spannable);
        while (matcher.find()) {
            boolean set = true;
            for (ImageSpan span : spannable.getSpans(matcher.start(), matcher.end(), ImageSpan.class)) {
                if (spannable.getSpanStart(span) >= matcher.start()
                        && spannable.getSpanEnd(span) <= matcher.end()
                        ) {
                    spannable.removeSpan(span);
                } else {
                    set = false;
                    break;
                }
            }
            String resname = spannable.subSequence(matcher.start(1), matcher.end(1)).toString().trim();
            //int id = context.getResources().getIdentifier(resname, "raw", context.getPackageName());
            Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher);
            try {
                bitmap = BitmapFactory.decodeStream(context.getResources().getAssets().open(resname));
            } catch (IOException e) {}
            if (set) {
                hasChanges = true;
                spannable.setSpan(  new ImageSpan(context, bitmap),
                        matcher.start(),
                        matcher.end(),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                );
            }
        }

        return hasChanges;
    }
    private static Spannable getTextWithImages(Context context, CharSequence text) {
        Spannable spannable = spannableFactory.newSpannable(text);
        addImages(context, spannable);
        return spannable;
    }
}

package com.symantec.searchactivity.adapters;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.symantec.searchactivity.R;
import com.symantec.searchactivity.models.ImageResult;

import org.w3c.dom.Text;

import java.util.List;

public class ImageResultArrayAdapter extends ArrayAdapter<ImageResult> {
    public ImageResultArrayAdapter(Context context, List<ImageResult> images) {
        super (context, R.layout.item_image_result, images);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get data item at current position
        ImageResult imageResult = getItem(position);
        // Check existing view being used

        if (convertView == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(getContext());
            convertView = layoutInflater.inflate(R.layout.item_image_result, parent, false);
        }

        // Find the imageView within the created or reused view
        ImageView ivImage = (ImageView) convertView.findViewById(R.id.ivImage);
        ivImage.setImageResource(0);

        // Get title
        TextView tvTitle = (TextView) convertView.findViewById(R.id.tvTitle);
        tvTitle.setText(Html.fromHtml(imageResult.getTitle()));

        Picasso.with(getContext()).load(imageResult.getThumbUrl()).into(ivImage);
        return convertView;
    }
}

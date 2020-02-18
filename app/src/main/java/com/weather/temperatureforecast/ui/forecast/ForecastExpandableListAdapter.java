package com.weather.temperatureforecast.ui.forecast;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;

import androidx.databinding.DataBindingUtil;

import com.weather.temperatureforecast.R;
import com.weather.temperatureforecast.databinding.ListGroupBinding;
import com.weather.temperatureforecast.databinding.ListItemBinding;
import com.weather.temperatureforecast.model.ExpandableDetails;
import com.weather.temperatureforecast.model.ExpandableHeader;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;

public class ForecastExpandableListAdapter extends BaseExpandableListAdapter {

    private Context mContext;
    private List<ExpandableHeader> mExpandableHeaders;
    private LinkedHashMap<ExpandableHeader, List<ExpandableDetails>> mExpandableDetails;

    ForecastExpandableListAdapter(Context context, List<ExpandableHeader> expandableHeaders,
                                  LinkedHashMap<ExpandableHeader, List<ExpandableDetails>> expandableDetails) {
        mContext = context;
        mExpandableHeaders = expandableHeaders;
        mExpandableDetails = expandableDetails;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return mExpandableHeaders.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return Objects.requireNonNull(mExpandableDetails.get(this.mExpandableHeaders.get(groupPosition)))
                .get(childPosition);
    }

    @Override
    public int getGroupCount() {
        return mExpandableHeaders.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return Objects.requireNonNull(mExpandableDetails.get(mExpandableHeaders.get(groupPosition))).size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getGroupView(final int groupPosition, final boolean isExpanded, View convertView, ViewGroup viewGroup) {
        ExpandableHeader expandableHeader = (ExpandableHeader) getGroup(groupPosition);
            LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert layoutInflater != null;
        convertView = layoutInflater.inflate(R.layout.list_group, null);
        ListGroupBinding binding = DataBindingUtil.bind(convertView);

        assert binding != null;
        binding.tvDay.setText(expandableHeader.getDay());
        binding.tvWeather.setText(expandableHeader.getWeather());
        binding.tvTemperatureMinMax.setText(expandableHeader.getMinMaxTemp());

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ExpandableDetails expandableDetails = (ExpandableDetails) getChild(groupPosition, childPosition);
        LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert layoutInflater != null;
        convertView = layoutInflater.inflate(R.layout.list_item, null);
        ListItemBinding binding = DataBindingUtil.bind(convertView);

        assert binding != null;
        binding.tvCity.setText(expandableDetails.getCityName());
        binding.tvDateTime.setText(expandableDetails.getDateAndTime().substring(0, 5) + " Hrs");
        binding.tvTemperature.setText(expandableDetails.getTemperature());
        binding.tvWeather.setText(expandableDetails.getWeather());
        binding.tvWind.setText(expandableDetails.getWind());
        binding.tvTemperatureMinMax.setText(expandableDetails.getMinmaxTemp() + "C");

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

}

package cz.kinst.jakub.sample.viewmodelbinding;

import android.content.Context;
import android.databinding.Bindable;
import android.location.Location;
import android.location.LocationManager;
import android.view.View;

import cz.kinst.jakub.sample.viewmodelbinding.databinding.ActivityMainBinding;
import cz.kinst.jakub.sample.viewmodelbinding.weather.WeatherApiProvider;
import cz.kinst.jakub.sample.viewmodelbinding.weather.WeatherConfig;
import cz.kinst.jakub.sample.viewmodelbinding.weather.WeatherData;
import cz.kinst.jakub.viewmodelbinding.BaseViewModel;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;


/**
 * Created by jakubkinst on 10/11/15.
 */
public class MainViewModel extends BaseViewModel<ActivityMainBinding> implements Callback<WeatherData> {

	private WeatherData mWeatherData;
	private Call<WeatherData> mWeatherCall;
	private boolean mLoading = false;


	public MainViewModel() {
	}


	@Override
	public void onViewAttached(boolean firstAttachment) {
		super.onViewAttached(firstAttachment);
		if(firstAttachment)
			refreshWeather(null);
	}


	public void refreshWeather(View view) {
		LocationManager locationManager = (LocationManager) getView().getContext().getSystemService(Context.LOCATION_SERVICE);
		Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		if(mWeatherCall != null)
			mWeatherCall.cancel();
		mWeatherCall = WeatherApiProvider.get().getWeatherData(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude(), WeatherConfig.WEATHER_APP_ID);
		mWeatherCall.enqueue(this);
		mLoading = true;
		notifyPropertyChanged(cz.kinst.jakub.sample.viewmodelbinding.BR.loading);
	}


	@Override
	public void onModelRemoved() {
		super.onModelRemoved();
		mWeatherCall.cancel();
	}


	@Bindable
	public boolean isLoading() {
		return mLoading;
	}


	@Override
	public void onResponse(Response<WeatherData> response, Retrofit retrofit) {
		mWeatherData = response.body();
		notifyPropertyChanged(cz.kinst.jakub.sample.viewmodelbinding.BR.weatherData);
		mLoading = false;
		notifyPropertyChanged(cz.kinst.jakub.sample.viewmodelbinding.BR.loading);
	}


	@Bindable
	public WeatherData getWeatherData() {
		return mWeatherData;
	}


	@Override
	public void onFailure(Throwable t) {

	}
}

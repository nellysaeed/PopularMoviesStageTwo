package com.nadeveloper.popularmoviesfinal.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import com.nadeveloper.popularmoviesfinal.R;
import com.nadeveloper.popularmoviesfinal.data.AppDatabase;
import com.nadeveloper.popularmoviesfinal.model.ApiResponse;
import com.nadeveloper.popularmoviesfinal.model.Movie;
import com.nadeveloper.popularmoviesfinal.rest.ApiClient;
import com.nadeveloper.popularmoviesfinal.rest.ServiceGenerator;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.nadeveloper.popularmoviesfinal.MainActivity.NO_INTERNET;

public class MainViewModel extends AndroidViewModel {
    private MutableLiveData<List<Movie>> popularMovies;
    private MutableLiveData<List<Movie>> highestMovies;
    private LiveData<List<Movie>> favoriteMovies;
    private MutableLiveData<Integer> status;
    private AppDatabase database;
    private ApiClient apiClient;

    public MainViewModel(@NonNull Application application) {
        super(application);
        database = AppDatabase.getDatabase(getApplication());
        apiClient = ServiceGenerator.createService(ApiClient.class);
    }

    public LiveData<List<Movie>> getPopularMovies() {
        if (popularMovies == null) {
            popularMovies = new MutableLiveData<>();
            loadMovies(0, 1);
        }
        return popularMovies;
    }

    public LiveData<List<Movie>> getHighestMovies() {
        if (highestMovies == null) {
            highestMovies = new MutableLiveData<>();
            loadMovies(1, 1);
        }
        return highestMovies;
    }

    public LiveData<List<Movie>> getFavoriteMovies() {
        if (favoriteMovies == null) {
            favoriteMovies = new MutableLiveData<>();
            getFavoritesFromDatabase();
        }
        return favoriteMovies;
    }

    public MutableLiveData<Integer> getStatus() {
        if (status == null) {
            status = new MutableLiveData<>();
            status.setValue(0);
        }
        return status;
    }

    public void loadMovies(int sorting, int page) {

        if (sorting == 0) {
            Call<ApiResponse<Movie>> call = apiClient.getPopularMovies(
                    getApplication().getString(R.string.language),
                    String.valueOf(page));

            call.enqueue(new Callback<ApiResponse<Movie>>() {
                @Override
                public void onResponse(Call<ApiResponse<Movie>> call,
                                       Response<ApiResponse<Movie>> response) {
                    if (response.isSuccessful()) {
                        List<Movie> result = response.body().results;
                        List<Movie> value = popularMovies.getValue();
                        if (value == null || value.isEmpty()) {
                            popularMovies.setValue(result);
                        } else {
                            value.addAll(result);
                            popularMovies.setValue(value);
                        }
                        status.setValue(0);
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse<Movie>> call, Throwable t) {
                    popularMovies = null;
                    status.setValue(NO_INTERNET);
                }
            });
        } else if (sorting == 1) {
            Call<ApiResponse<Movie>> call = apiClient.getTopRatedMovies(
                    getApplication().getString(R.string.language),
                    String.valueOf(page));

            call.enqueue(new Callback<ApiResponse<Movie>>() {
                @Override
                public void onResponse(Call<ApiResponse<Movie>> call,
                                       Response<ApiResponse<Movie>> response) {
                    if (response.isSuccessful()) {
                        List<Movie> result = response.body().results;
                        List<Movie> value = highestMovies.getValue();
                        if (value == null || value.isEmpty()) {
                            highestMovies.setValue(result);
                        } else {
                            value.addAll(result);
                            highestMovies.setValue(value);
                        }
                        status.setValue(0);
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse<Movie>> call, Throwable t) {
                    highestMovies = null;
                    status.setValue(NO_INTERNET);
                }
            });
        }
    }

    private void getFavoritesFromDatabase() {
        favoriteMovies = database.movieDao().getAll();
    }
}
package com.nikola.jakshic.truesight;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.nikola.jakshic.truesight.model.Hero;
import com.nikola.jakshic.truesight.repository.HeroRepository;

import java.util.List;

import javax.inject.Inject;

public class HeroFragmentViewModel extends ViewModel {

    private MutableLiveData<List<Hero>> list;
    private HeroRepository repository;
    private MutableLiveData<Boolean> loading;

    @Inject
    public HeroFragmentViewModel(HeroRepository repository) {
        this.repository = repository;
        list = new MutableLiveData<>();
        loading = new MutableLiveData<>();
        loading.setValue(false);
    }

    public void fetchHeroes(long id) {
        repository.fetchHeroes(list, loading, id);
    }

    public MutableLiveData<List<Hero>> getHeroes() {
        return list;
    }

    public MutableLiveData<Boolean> isLoading() {
        return loading;
    }
}
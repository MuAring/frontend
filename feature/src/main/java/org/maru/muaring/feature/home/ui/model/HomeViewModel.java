package org.maru.muaring.feature.home.ui.model;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import org.maru.muaring.core.util.Resource;
import org.maru.muaring.data.api.dto.MemberSettingsResponse;
import org.maru.muaring.data.repository.MemberRepository;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class HomeViewModel extends ViewModel {

    private final MemberRepository memberRepository;
    private final MutableLiveData<Resource<MemberSettingsResponse>> mySettings = new MutableLiveData<>();

    public LiveData<Resource<MemberSettingsResponse>> getMySettings() {
        return mySettings;
    }

    @Inject
    public HomeViewModel(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
        loadMySettings();
    }

    private void loadMySettings() {
        memberRepository.getMySettings().observeForever(resource -> {
            mySettings.setValue(resource);  // Resource 그대로 전달
        });
    }
}


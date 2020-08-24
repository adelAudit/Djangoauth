package com.adel.audit.djangoauth.listeners;

import com.adel.audit.djangoauth.data.Property;

import java.util.List;

public interface OnCompleteListener {
    void onComplete(List<Property> proprties);
}

package com.iss.eventorium.shared.utils;

import com.iss.eventorium.shared.models.ImagePath;

import java.util.List;

public interface ImageHolder {
    List<ImagePath> getImagePaths();
    Long getId();
}

package com.sixsprints.notification.service;

import java.util.concurrent.Future;

import com.sixsprints.notification.dto.ShortURLDto;

public interface URLShorteningService {

  Future<ShortURLDto> shortenAsync(String url);

  ShortURLDto shorten(String url);

}

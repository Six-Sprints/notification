package com.sixsprints.notification.service.impl;

import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.sixsprints.json.dto.Mapping;
import com.sixsprints.json.util.ApiFactory;
import com.sixsprints.notification.dto.ShortURLDto;
import com.sixsprints.notification.service.URLShorteningApiService;
import com.sixsprints.notification.service.URLShorteningService;

import retrofit2.Call;

public class URLShorteningServiceImpl implements URLShorteningService {

  private URLShorteningApiService urlShorteningService;

  private String apiKey;

  public URLShorteningServiceImpl(String apiKey) {
    super();
    this.apiKey = apiKey;
    urlShorteningService = ApiFactory.create(URLShorteningApiService.class, URLShorteningApiService.BASE_URL);
  }

  @Override
  public Future<ShortURLDto> shortenAsync(String url) {
    return Executors.newSingleThreadExecutor().submit(() -> shorten(url));
  }

  @Override
  public ShortURLDto shorten(String url) {
    try {
      Call<String> call = urlShorteningService.sendSms(this.apiKey, url);

      ShortURLDto dto = ApiFactory.makeCallAndTransform(call, ShortURLDto.class,
        Mapping.builder().rootElement("url").build());

      return dto;
    } catch (Exception ex) {
      throw new IllegalArgumentException(ex.getMessage());
    }
  }

}

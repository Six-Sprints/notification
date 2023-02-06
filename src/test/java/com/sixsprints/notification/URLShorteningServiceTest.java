package com.sixsprints.notification;

import java.io.UnsupportedEncodingException;
import java.util.concurrent.ExecutionException;

import org.junit.jupiter.api.Test;

import com.sixsprints.notification.dto.ShortURLDto;
import com.sixsprints.notification.service.impl.URLShorteningServiceImpl;

public class URLShorteningServiceTest {

  private String apiKey = "";
  private URLShorteningServiceImpl urlShorteningServiceImpl = new URLShorteningServiceImpl(apiKey);

  @Test
  public void testShouldShortenURL() throws InterruptedException, ExecutionException, UnsupportedEncodingException {

    String url = "https://jyotihospital.auth.proclinic.in/auth/login?redirectUrl=https://raman.proclinic.in/home/callback&redirectPath=/app/chat&mobileNumber=";
    ShortURLDto resp = urlShorteningServiceImpl.shorten(url);
    System.out.println(resp);
  }

}

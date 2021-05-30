package com.sixsprints.notification;

import java.io.UnsupportedEncodingException;
import java.util.concurrent.ExecutionException;

import com.sixsprints.notification.dto.ShortURLDto;
import com.sixsprints.notification.service.impl.URLShorteningServiceImpl;

import junit.framework.TestCase;

public class URLShorteningServiceTest extends TestCase {

  private String apiKey = "";
  private URLShorteningServiceImpl urlShorteningServiceImpl = new URLShorteningServiceImpl(apiKey);

  public void testShouldShortenURL() throws InterruptedException, ExecutionException, UnsupportedEncodingException {

    String url = "https://jyotihospital.auth.proclinic.in/auth/login?redirectUrl=https://raman.proclinic.in/home/callback&redirectPath=/app/chat&mobileNumber=";
    ShortURLDto resp = urlShorteningServiceImpl.shorten(url);
    System.out.println(resp);
  }

}

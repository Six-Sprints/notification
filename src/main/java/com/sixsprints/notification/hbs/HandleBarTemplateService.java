package com.sixsprints.notification.hbs;

import java.io.IOException;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.io.ClassPathTemplateLoader;
import com.github.jknack.handlebars.io.TemplateLoader;

public class HandleBarTemplateService {

  private static TemplateLoader loader = new ClassPathTemplateLoader("/hbs", ".hbs");
  private static Handlebars handlebars = new Handlebars(loader);

  static {
    handlebars.setInfiniteLoops(true);
  }

  public static String parse(String templateId, Object object) throws IOException {
    return handlebars
      .compile(templateId)
      .apply(object);
  }

}

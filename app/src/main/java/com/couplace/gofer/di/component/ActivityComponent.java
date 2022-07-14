package com.couplace.gofer.di.component;

import com.couplace.gofer.auth.FacebookLoginActivity;
import com.couplace.gofer.auth.GoogleSignInActivity;
import com.couplace.gofer.di.PerActivity;
import com.couplace.gofer.di.module.ActivityModule;
import com.couplace.gofer.product.ProductActivity;

import dagger.Component;


@PerActivity
@Component(dependencies = ApplicationComponent.class, modules = ActivityModule.class)
public interface ActivityComponent {

    void inject(ProductActivity productActivity);

    void inject(GoogleSignInActivity googleSignInActivity);

    void inject(FacebookLoginActivity facebookLoginActivity);

}

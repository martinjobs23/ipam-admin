package com.ceit.admin.common;

import com.ceit.admin.controller.TestController;
import com.ceit.ioc.annotations.Autowired;
import com.ceit.ioc.annotations.Component;
import com.ceit.ioc.annotations.Scope;

@Component
@Scope("Prototype")
public class ScopeTest {
    @Autowired
    private TestController testController;
}

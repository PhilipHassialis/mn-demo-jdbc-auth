package com.hassialis.philip;

import io.micronaut.http.annotation.*;

@Controller("/mnDemoJdbcAuth")
public class MnDemoJdbcAuthController {

    @Get(uri="/", produces="text/plain")
    public String index() {
        return "Example Response";
    }
}
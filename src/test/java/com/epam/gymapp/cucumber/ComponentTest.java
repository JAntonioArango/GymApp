package com.epam.gymapp.cucumber;

import io.cucumber.junit.platform.engine.Constants;
import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features/component")
@ConfigurationParameter(
    key = Constants.GLUE_PROPERTY_NAME,
    value =
        "com.epam.gymapp.cucumber.config,com.epam.gymapp.cucumber.component.config,com.epam.gymapp.cucumber.component")
@ConfigurationParameter(
    key = Constants.PLUGIN_PROPERTY_NAME,
    value = "pretty,html:target/cucumber-reports/component")
public class ComponentTest {}

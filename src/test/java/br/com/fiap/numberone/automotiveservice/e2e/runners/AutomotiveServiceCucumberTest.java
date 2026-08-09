package br.com.fiap.numberone.automotiveservice.e2e.runners;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

import static io.cucumber.junit.platform.engine.Constants.GLUE_PROPERTY_NAME;
import static io.cucumber.junit.platform.engine.Constants.PLUGIN_PROPERTY_NAME;

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features/automotiveservice")
@ConfigurationParameter(
        key = GLUE_PROPERTY_NAME,
        value = "br.com.fiap.numberone.automotiveservice.e2e"
)
@ConfigurationParameter(
        key = PLUGIN_PROPERTY_NAME,
        value = "pretty, html:target/cucumber-reports/automotiveservice/index.html, json:target/cucumber-reports/automotiveservice/cucumber.json"
)
public class AutomotiveServiceCucumberTest {
}

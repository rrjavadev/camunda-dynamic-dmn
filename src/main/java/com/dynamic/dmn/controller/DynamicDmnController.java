package com.dynamic.dmn.controller;

import com.dynamic.dmn.service.DynamicDmnGenerator;
import lombok.AllArgsConstructor;
import org.camunda.bpm.engine.DecisionService;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.variable.VariableMap;
import org.camunda.bpm.engine.variable.Variables;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@AllArgsConstructor
public class DynamicDmnController {

    DynamicDmnGenerator dynamicDmnGenerator;
    ProcessEngine processEngine;

    @PostMapping("generate_and_deploy")
    public void generateAndDeployDmn(){
        dynamicDmnGenerator.generateAndDeployDmn("dynamic_dmn_template", "dynamic_dmn");
    }

    @PostMapping("run_decisions/{decision_key}")
    public List<Map<String, Object>> runDecisions(@PathVariable("decision_key") String decisionKey,
                                                  @RequestBody Map<String, Object> inputValues){

        DecisionService decisionService = processEngine.getDecisionService();
        VariableMap inputVariables = Variables.fromMap(inputValues);

        return decisionService.evaluateDecisionTableByKey(decisionKey, inputVariables).getResultList();
    }
}

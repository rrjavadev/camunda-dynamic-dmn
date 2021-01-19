package com.dynamic.dmn.service;

import com.dynamic.dmn.dto.MyRuleDto;
import lombok.AllArgsConstructor;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.model.dmn.Dmn;
import org.camunda.bpm.model.dmn.DmnModelInstance;
import org.camunda.bpm.model.dmn.instance.Decision;
import org.camunda.bpm.model.dmn.instance.DecisionTable;
import org.camunda.bpm.model.dmn.instance.InputEntry;
import org.camunda.bpm.model.dmn.instance.OutputEntry;
import org.camunda.bpm.model.dmn.instance.Rule;
import org.camunda.bpm.model.dmn.instance.Text;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

@Service
@AllArgsConstructor
public class DynamicDmnGenerator {

    private static final String TEMPLATE_DIRECTORY = "/templates/";
    private static final String DYNAMIC_DMN = "dynamic_dmn_decision_table";
    private ProcessEngine processEngine;

    public void generateAndDeployDmn(String templateName, String decisionId) {

        DmnModelInstance dmnModelInstance = getDmnModelInstance(templateName);
        Decision dmnDecision = dmnModelInstance.getModelElementById(templateName);
        dmnDecision.setId(decisionId);

        DecisionTable decisionTable = dmnModelInstance.getModelElementById(DYNAMIC_DMN);

        List<MyRuleDto> myRules = getMyDynamicRules();

        myRules.forEach(e -> {
            Rule rule = newRule(dmnModelInstance, e);
            decisionTable.getRules().add(rule);
        });

        processEngine.getRepositoryService()
                .createDeployment()
                .name(decisionId)
                .addString(decisionId + ".dmn", Dmn.convertToString(dmnModelInstance))
                .enableDuplicateFiltering(false)
                .deploy();
    }

    private List<MyRuleDto> getMyDynamicRules() {
        return Arrays.asList(MyRuleDto.builder()
                .input1("<3000")
                .input2("<= 3")
                .input3("\"abc\"")
                .output("\"success\"")
                .build(),
                MyRuleDto.builder()
                        .input1("> 3000")
                        .input2("<= 7")
                        .input3("\"another one\"")
                        .output("\"success\"")
                        .build()
                );
    }

    private Rule newRule(DmnModelInstance dmnModelInstance, MyRuleDto myRule) {

        Rule rule = dmnModelInstance.newInstance(Rule.class);

        InputEntry inputEntry1 = createInputEntry(dmnModelInstance, myRule.getInput1());
        InputEntry inputEntry2 = createInputEntry(dmnModelInstance, myRule.getInput2());
        InputEntry inputEntry3 = createInputEntry(dmnModelInstance, myRule.getInput3());

        rule.getInputEntries().add(inputEntry1);
        rule.getInputEntries().add(inputEntry2);
        rule.getInputEntries().add(inputEntry3);

        OutputEntry outputEntry = createOutputEntry(dmnModelInstance, myRule.getOutput());
        rule.getOutputEntries().add(outputEntry);

        return rule;
    }

    private InputEntry createInputEntry(DmnModelInstance dmnModelInstance, String ruleExpression) {

        Text text = dmnModelInstance.newInstance(Text.class);
        text.setTextContent(ruleExpression);

        InputEntry inputEntry = dmnModelInstance.newInstance(InputEntry.class);
        inputEntry.setText(text);
        return inputEntry;
    }

    private OutputEntry createOutputEntry(DmnModelInstance dmnModelInstance, String ruleExpression) {

        Text text = dmnModelInstance.newInstance(Text.class);
        text.setTextContent(ruleExpression);

        OutputEntry outputEntry = dmnModelInstance.newInstance(OutputEntry.class);
        outputEntry.setText(text);
        return outputEntry;
    }

    private DmnModelInstance getDmnModelInstance(String templateName) {
        String template = TEMPLATE_DIRECTORY + templateName + ".xml";
        InputStream inputStream = getClass().getResourceAsStream(template);
        return Dmn.readModelFromStream(inputStream);
    }
}

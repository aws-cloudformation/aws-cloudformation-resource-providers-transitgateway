package com.aws.ec2.transitgatewayattachment.workflow.update;

import com.aws.ec2.transitgatewayattachment.CallbackContext;
import com.aws.ec2.transitgatewayattachment.Configuration;
import com.aws.ec2.transitgatewayattachment.ResourceModel;
import org.json.JSONArray;
import org.json.JSONObject;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.cloudformation.exceptions.CfnInvalidRequestException;
import software.amazon.cloudformation.proxy.*;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ValidatePropertiesCheck {

    AmazonWebServicesClientProxy proxy;
    ResourceHandlerRequest<ResourceModel> request;
    CallbackContext callbackContext;
    ProxyClient<Ec2Client> client;
    Logger logger;

    public ValidatePropertiesCheck(
            AmazonWebServicesClientProxy proxy,
            ResourceHandlerRequest<ResourceModel> request,
            CallbackContext callbackContext,
            ProxyClient<Ec2Client> client,
            Logger logger
    ) {
        this.proxy = proxy;
        this.request = request;
        this.callbackContext = callbackContext;
        this.client = client;
        this.logger = logger;
    }

    public ProgressEvent<ResourceModel, CallbackContext> run(ProgressEvent<ResourceModel, CallbackContext> progress) {
        if(this.request.getDesiredResourceState().getId() == null) {
            CfnInvalidRequestException exception =  new CfnInvalidRequestException(this.request.getDesiredResourceState().getPrimaryIdentifier() + ": cannot NULL.");
            return ProgressEvent.defaultFailureHandler(exception, HandlerErrorCode.InvalidRequest);
        }
        else if(this.invalidProperties().isEmpty()) {
            return progress;
        } else {
            CfnInvalidRequestException exception =  new CfnInvalidRequestException(this.invalidProperties().toString() + ": cannot be changed and can only be used to create " + ResourceModel.TYPE_NAME + ".");
            return ProgressEvent.defaultFailureHandler(exception, HandlerErrorCode.InvalidRequest);
        }
    }

    private List<String> createOnlyProperties() {
        //PULL CREATE ONLY PROPERTIES FROM THE JSON SCHEMA TO SEE IF THERE ARE ANY DEFINED IN THE REQUEST THAT SHOULD NOT BE

        JSONObject config = new Configuration().resourceSchemaJSONObject();
        if(config.has("createOnlyProperties")) {
            JSONArray props = config.getJSONArray("createOnlyProperties");
            List<String> list = new ArrayList<>();
            for(int i = 0; i < props.length(); i++){
                String prop = props.getString(i).replace("/properties/", "");
                list.add(prop);
            }
            return list;
        } else {
            return new ArrayList<>();
        }
    }

    private List<String> invalidProperties() {
        ResourceModel model = this.request.getDesiredResourceState();
        return this.createOnlyProperties().stream().filter((prop) -> {
            try {
                Method method = model.getClass().getMethod("get" + prop);
                return method.invoke(model) != null;
            } catch(Exception e ) {
                return false;
            }
        }).collect(Collectors.toList());
    }

}

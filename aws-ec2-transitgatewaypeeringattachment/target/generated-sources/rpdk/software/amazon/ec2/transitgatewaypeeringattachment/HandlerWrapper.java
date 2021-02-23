// This is a generated file. Modifications will be overwritten.
package software.amazon.ec2.transitgatewaypeeringattachment;

import com.amazonaws.AmazonServiceException;
import software.amazon.awssdk.regions.PartitionMetadata;
import software.amazon.awssdk.regions.Region;
import software.amazon.cloudformation.Action;
import software.amazon.cloudformation.exceptions.BaseHandlerException;
import software.amazon.cloudformation.LambdaWrapper;
import software.amazon.cloudformation.loggers.LambdaLogPublisher;
import software.amazon.cloudformation.metrics.MetricsPublisher;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.CallbackAdapter;
import software.amazon.cloudformation.proxy.HandlerErrorCode;
import software.amazon.cloudformation.proxy.HandlerRequest;
import software.amazon.cloudformation.proxy.LoggerProxy;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.RequestContext;
import software.amazon.cloudformation.proxy.RequestData;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;
import software.amazon.cloudformation.proxy.ResourceHandlerTestPayload;
import software.amazon.cloudformation.resource.SchemaValidator;
import software.amazon.cloudformation.resource.Serializer;
import software.amazon.cloudformation.scheduler.CloudWatchScheduler;
import com.amazonaws.services.lambda.runtime.Context;
import com.fasterxml.jackson.core.type.TypeReference;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.json.JSONObject;


public final class HandlerWrapper extends LambdaWrapper<ResourceModel, CallbackContext> {

    private final Configuration configuration = new Configuration();
    private JSONObject resourceSchema;
    private final Map<Action, BaseHandler<CallbackContext>> handlers = new HashMap<>();
    private final static TypeReference<HandlerRequest<ResourceModel, CallbackContext>> REQUEST_REFERENCE =
        new TypeReference<HandlerRequest<ResourceModel, CallbackContext>>() {};
    private final static TypeReference<ResourceModel> TYPE_REFERENCE =
        new TypeReference<ResourceModel>() {};
    private final static TypeReference<ResourceHandlerTestPayload<ResourceModel, CallbackContext>> TEST_ENTRY_TYPE_REFERENCE =
        new TypeReference<ResourceHandlerTestPayload<ResourceModel, CallbackContext>>() {};


    public HandlerWrapper() {
        initialiseHandlers();
    }

    private void initialiseHandlers() {
        handlers.put(Action.CREATE, new CreateHandler());
        handlers.put(Action.READ, new ReadHandler());
        handlers.put(Action.UPDATE, new UpdateHandler());
        handlers.put(Action.DELETE, new DeleteHandler());
        handlers.put(Action.LIST, new ListHandler());
    }

    @Override
    public ProgressEvent<ResourceModel, CallbackContext> invokeHandler(
                final AmazonWebServicesClientProxy proxy,
                final ResourceHandlerRequest<ResourceModel> request,
                final Action action,
                final CallbackContext callbackContext) {

        final String actionName = (action == null) ? "<null>" : action.toString(); // paranoia
        if (!handlers.containsKey(action))
            throw new RuntimeException("Unknown action " + actionName);

        final BaseHandler<CallbackContext> handler = handlers.get(action);

        loggerProxy.log(String.format("[%s] invoking handler...", actionName));
        final ProgressEvent<ResourceModel, CallbackContext> result = handler.handleRequest(proxy, request, callbackContext, loggerProxy);
        loggerProxy.log(String.format("[%s] handler invoked", actionName));
        return result;
    }

    public void testEntrypoint(
            final InputStream inputStream,
            final OutputStream outputStream,
            final Context context) throws IOException {

        this.loggerProxy = new LoggerProxy();
        this.loggerProxy.addLogPublisher(new LambdaLogPublisher(context.getLogger()));

        ProgressEvent<ResourceModel, CallbackContext> response = ProgressEvent.failed(null, null, HandlerErrorCode.InternalFailure, "Uninitialized");
        try {
            final String input = IOUtils.toString(inputStream, "UTF-8");
            final ResourceHandlerTestPayload<ResourceModel, CallbackContext> payload =
                this.serializer.deserialize(
                    input,
                    TEST_ENTRY_TYPE_REFERENCE);

            final AmazonWebServicesClientProxy proxy = new AmazonWebServicesClientProxy(
                loggerProxy, payload.getCredentials(), () -> (long) context.getRemainingTimeInMillis());

            response = invokeHandler(proxy, payload.getRequest(), payload.getAction(), payload.getCallbackContext());
        } catch (final BaseHandlerException e) {
            response = ProgressEvent.defaultFailureHandler(e, e.getErrorCode());
        } catch (final AmazonServiceException e) {
            response = ProgressEvent.defaultFailureHandler(e, HandlerErrorCode.GeneralServiceException);
        } catch (final Throwable e) {
            e.printStackTrace();
            response = ProgressEvent.defaultFailureHandler(e, HandlerErrorCode.InternalFailure);
        } finally {
            writeResponse(outputStream, response);
        }
    }

    @Override
    public JSONObject provideResourceSchemaJSONObject() {
        if (resourceSchema == null) {
            resourceSchema = this.configuration.resourceSchemaJSONObject();
        }
        return resourceSchema;
    }

    @Override
    public Map<String, String> provideResourceDefinedTags(final ResourceModel resourceModel) {
        return this.configuration.resourceDefinedTags(resourceModel);
    }

    @Override
    protected ResourceHandlerRequest<ResourceModel> transform(final HandlerRequest<ResourceModel, CallbackContext> request) throws IOException {
        final RequestData<ResourceModel> requestData = request.getRequestData();

        return ResourceHandlerRequest.<ResourceModel>builder()
            .clientRequestToken(request.getBearerToken())
            .desiredResourceState(requestData.getResourceProperties())
            .previousResourceState(requestData.getPreviousResourceProperties())
            .desiredResourceTags(getDesiredResourceTags(request))
            .systemTags(request.getRequestData().getSystemTags())
            .awsAccountId(request.getAwsAccountId())
            .logicalResourceIdentifier(request.getRequestData().getLogicalResourceId())
            .nextToken(request.getNextToken())
            .region(request.getRegion())
            .awsPartition(PartitionMetadata.of(Region.of(request.getRegion())).id())
            .build();
    }

    @Override
    protected TypeReference<HandlerRequest<ResourceModel, CallbackContext>> getTypeReference() {
        return REQUEST_REFERENCE;
    }

    @Override
    protected TypeReference<ResourceModel> getModelTypeReference() {
        return TYPE_REFERENCE;
    }
}

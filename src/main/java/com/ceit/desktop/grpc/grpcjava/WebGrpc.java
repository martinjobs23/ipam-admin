package com.ceit.desktop.grpc.grpcjava;

import com.ceit.desktop.grpc.controlCenter.*;

import static io.grpc.MethodDescriptor.generateFullMethodName;
import static io.grpc.stub.ClientCalls.asyncUnaryCall;
import static io.grpc.stub.ClientCalls.blockingUnaryCall;
import static io.grpc.stub.ClientCalls.futureUnaryCall;
import static io.grpc.stub.ServerCalls.asyncUnaryCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall;

/**
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.34.1)",
    comments = "Source: web.proto")
public final class WebGrpc {

  private WebGrpc() {}

  public static final String SERVICE_NAME = "web.Web";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<DevRegisterRequest,
          DevRegisterReply> getDevRegisterCheckMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "DevRegisterCheck",
      requestType = DevRegisterRequest.class,
      responseType = DevRegisterReply.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<DevRegisterRequest,
      DevRegisterReply> getDevRegisterCheckMethod() {
    io.grpc.MethodDescriptor<DevRegisterRequest, DevRegisterReply> getDevRegisterCheckMethod;
    if ((getDevRegisterCheckMethod = WebGrpc.getDevRegisterCheckMethod) == null) {
      synchronized (WebGrpc.class) {
        if ((getDevRegisterCheckMethod = WebGrpc.getDevRegisterCheckMethod) == null) {
          WebGrpc.getDevRegisterCheckMethod = getDevRegisterCheckMethod =
              io.grpc.MethodDescriptor.<DevRegisterRequest, DevRegisterReply>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "DevRegisterCheck"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  DevRegisterRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  DevRegisterReply.getDefaultInstance()))
              .setSchemaDescriptor(new WebMethodDescriptorSupplier("DevRegisterCheck"))
              .build();
        }
      }
    }
    return getDevRegisterCheckMethod;
  }

  private static volatile io.grpc.MethodDescriptor<DevUnRegisterRequest,
          DevUnRegisterReply> getDevUnRegisterMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "DevUnRegister",
      requestType = DevUnRegisterRequest.class,
      responseType = DevUnRegisterReply.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<DevUnRegisterRequest,
      DevUnRegisterReply> getDevUnRegisterMethod() {
    io.grpc.MethodDescriptor<DevUnRegisterRequest, DevUnRegisterReply> getDevUnRegisterMethod;
    if ((getDevUnRegisterMethod = WebGrpc.getDevUnRegisterMethod) == null) {
      synchronized (WebGrpc.class) {
        if ((getDevUnRegisterMethod = WebGrpc.getDevUnRegisterMethod) == null) {
          WebGrpc.getDevUnRegisterMethod = getDevUnRegisterMethod =
              io.grpc.MethodDescriptor.<DevUnRegisterRequest, DevUnRegisterReply>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "DevUnRegister"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  DevUnRegisterRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  DevUnRegisterReply.getDefaultInstance()))
              .setSchemaDescriptor(new WebMethodDescriptorSupplier("DevUnRegister"))
              .build();
        }
      }
    }
    return getDevUnRegisterMethod;
  }

  private static volatile io.grpc.MethodDescriptor<FileDetailRequestByType,
      FileDetailRespone> getFileDetailByTypeMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "fileDetailByType",
      requestType = FileDetailRequestByType.class,
      responseType = FileDetailRespone.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<FileDetailRequestByType,
      FileDetailRespone> getFileDetailByTypeMethod() {
    io.grpc.MethodDescriptor<FileDetailRequestByType, FileDetailRespone> getFileDetailByTypeMethod;
    if ((getFileDetailByTypeMethod = WebGrpc.getFileDetailByTypeMethod) == null) {
      synchronized (WebGrpc.class) {
        if ((getFileDetailByTypeMethod = WebGrpc.getFileDetailByTypeMethod) == null) {
          WebGrpc.getFileDetailByTypeMethod = getFileDetailByTypeMethod =
              io.grpc.MethodDescriptor.<FileDetailRequestByType, FileDetailRespone>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "fileDetailByType"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  FileDetailRequestByType.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  FileDetailRespone.getDefaultInstance()))
              .setSchemaDescriptor(new WebMethodDescriptorSupplier("fileDetailByType"))
              .build();
        }
      }
    }
    return getFileDetailByTypeMethod;
  }

  private static volatile io.grpc.MethodDescriptor<FileDetailRequestByName,
      FileDetailRespone> getFileDetailByNameMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "fileDetailByName",
      requestType = FileDetailRequestByName.class,
      responseType = FileDetailRespone.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<FileDetailRequestByName,
      FileDetailRespone> getFileDetailByNameMethod() {
    io.grpc.MethodDescriptor<FileDetailRequestByName, FileDetailRespone> getFileDetailByNameMethod;
    if ((getFileDetailByNameMethod = WebGrpc.getFileDetailByNameMethod) == null) {
      synchronized (WebGrpc.class) {
        if ((getFileDetailByNameMethod = WebGrpc.getFileDetailByNameMethod) == null) {
          WebGrpc.getFileDetailByNameMethod = getFileDetailByNameMethod =
              io.grpc.MethodDescriptor.<FileDetailRequestByName, FileDetailRespone>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "fileDetailByName"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  FileDetailRequestByName.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  FileDetailRespone.getDefaultInstance()))
              .setSchemaDescriptor(new WebMethodDescriptorSupplier("fileDetailByName"))
              .build();
        }
      }
    }
    return getFileDetailByNameMethod;
  }

  private static volatile io.grpc.MethodDescriptor<UploadRequest,
      UploadRespond> getSoftwareRegisterMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "softwareRegister",
      requestType = UploadRequest.class,
      responseType = UploadRespond.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<UploadRequest,
      UploadRespond> getSoftwareRegisterMethod() {
    io.grpc.MethodDescriptor<UploadRequest, UploadRespond> getSoftwareRegisterMethod;
    if ((getSoftwareRegisterMethod = WebGrpc.getSoftwareRegisterMethod) == null) {
      synchronized (WebGrpc.class) {
        if ((getSoftwareRegisterMethod = WebGrpc.getSoftwareRegisterMethod) == null) {
          WebGrpc.getSoftwareRegisterMethod = getSoftwareRegisterMethod =
              io.grpc.MethodDescriptor.<UploadRequest, UploadRespond>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "softwareRegister"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  UploadRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  UploadRespond.getDefaultInstance()))
              .setSchemaDescriptor(new WebMethodDescriptorSupplier("softwareRegister"))
              .build();
        }
      }
    }
    return getSoftwareRegisterMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static WebStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<WebStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<WebStub>() {
        @Override
        public WebStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new WebStub(channel, callOptions);
        }
      };
    return WebStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static WebBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<WebBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<WebBlockingStub>() {
        @Override
        public WebBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new WebBlockingStub(channel, callOptions);
        }
      };
    return WebBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static WebFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<WebFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<WebFutureStub>() {
        @Override
        public WebFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new WebFutureStub(channel, callOptions);
        }
      };
    return WebFutureStub.newStub(factory, channel);
  }

  /**
   */
  public static abstract class WebImplBase implements io.grpc.BindableService {

    /**
     */
    public void devRegisterCheck(DevRegisterRequest request,
                                 io.grpc.stub.StreamObserver<DevRegisterReply> responseObserver) {
      asyncUnimplementedUnaryCall(getDevRegisterCheckMethod(), responseObserver);
    }

    /**
     */
    public void devUnRegister(DevUnRegisterRequest request,
                              io.grpc.stub.StreamObserver<DevUnRegisterReply> responseObserver) {
      asyncUnimplementedUnaryCall(getDevUnRegisterMethod(), responseObserver);
    }

    /**
     */
    public void fileDetailByType(FileDetailRequestByType request,
                                 io.grpc.stub.StreamObserver<FileDetailRespone> responseObserver) {
      asyncUnimplementedUnaryCall(getFileDetailByTypeMethod(), responseObserver);
    }

    /**
     */
    public void fileDetailByName(FileDetailRequestByName request,
                                 io.grpc.stub.StreamObserver<FileDetailRespone> responseObserver) {
      asyncUnimplementedUnaryCall(getFileDetailByNameMethod(), responseObserver);
    }

    /**
     */
    public void softwareRegister(UploadRequest request,
                                 io.grpc.stub.StreamObserver<UploadRespond> responseObserver) {
      asyncUnimplementedUnaryCall(getSoftwareRegisterMethod(), responseObserver);
    }

    @Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getDevRegisterCheckMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                DevRegisterRequest,
                DevRegisterReply>(
                  this, METHODID_DEV_REGISTER_CHECK)))
          .addMethod(
            getDevUnRegisterMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                DevUnRegisterRequest,
                DevUnRegisterReply>(
                  this, METHODID_DEV_UN_REGISTER)))
          .addMethod(
            getFileDetailByTypeMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                FileDetailRequestByType,
                FileDetailRespone>(
                  this, METHODID_FILE_DETAIL_BY_TYPE)))
          .addMethod(
            getFileDetailByNameMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                FileDetailRequestByName,
                FileDetailRespone>(
                  this, METHODID_FILE_DETAIL_BY_NAME)))
          .addMethod(
            getSoftwareRegisterMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                UploadRequest,
                UploadRespond>(
                  this, METHODID_SOFTWARE_REGISTER)))
          .build();
    }
  }

  /**
   */
  public static final class WebStub extends io.grpc.stub.AbstractAsyncStub<WebStub> {
    private WebStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @Override
    protected WebStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new WebStub(channel, callOptions);
    }

    /**
     */
    public void devRegisterCheck(DevRegisterRequest request,
                                 io.grpc.stub.StreamObserver<DevRegisterReply> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getDevRegisterCheckMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void devUnRegister(DevUnRegisterRequest request,
                              io.grpc.stub.StreamObserver<DevUnRegisterReply> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getDevUnRegisterMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void fileDetailByType(FileDetailRequestByType request,
                                 io.grpc.stub.StreamObserver<FileDetailRespone> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getFileDetailByTypeMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void fileDetailByName(FileDetailRequestByName request,
                                 io.grpc.stub.StreamObserver<FileDetailRespone> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getFileDetailByNameMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void softwareRegister(UploadRequest request,
                                 io.grpc.stub.StreamObserver<UploadRespond> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getSoftwareRegisterMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   */
  public static final class WebBlockingStub extends io.grpc.stub.AbstractBlockingStub<WebBlockingStub> {
    private WebBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @Override
    protected WebBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new WebBlockingStub(channel, callOptions);
    }

    /**
     */
    public DevRegisterReply devRegisterCheck(DevRegisterRequest request) {
      return blockingUnaryCall(
          getChannel(), getDevRegisterCheckMethod(), getCallOptions(), request);
    }

    /**
     */
    public DevUnRegisterReply devUnRegister(DevUnRegisterRequest request) {
      return blockingUnaryCall(
          getChannel(), getDevUnRegisterMethod(), getCallOptions(), request);
    }

    /**
     */
    public FileDetailRespone fileDetailByType(FileDetailRequestByType request) {
      return blockingUnaryCall(
          getChannel(), getFileDetailByTypeMethod(), getCallOptions(), request);
    }

    /**
     */
    public FileDetailRespone fileDetailByName(FileDetailRequestByName request) {
      return blockingUnaryCall(
          getChannel(), getFileDetailByNameMethod(), getCallOptions(), request);
    }

    /**
     */
    public UploadRespond softwareRegister(UploadRequest request) {
      return blockingUnaryCall(
          getChannel(), getSoftwareRegisterMethod(), getCallOptions(), request);
    }
  }

  /**
   */
  public static final class WebFutureStub extends io.grpc.stub.AbstractFutureStub<WebFutureStub> {
    private WebFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @Override
    protected WebFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new WebFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<DevRegisterReply> devRegisterCheck(
        DevRegisterRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getDevRegisterCheckMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<DevUnRegisterReply> devUnRegister(
        DevUnRegisterRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getDevUnRegisterMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<FileDetailRespone> fileDetailByType(
        FileDetailRequestByType request) {
      return futureUnaryCall(
          getChannel().newCall(getFileDetailByTypeMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<FileDetailRespone> fileDetailByName(
        FileDetailRequestByName request) {
      return futureUnaryCall(
          getChannel().newCall(getFileDetailByNameMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<UploadRespond> softwareRegister(
        UploadRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getSoftwareRegisterMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_DEV_REGISTER_CHECK = 0;
  private static final int METHODID_DEV_UN_REGISTER = 1;
  private static final int METHODID_FILE_DETAIL_BY_TYPE = 2;
  private static final int METHODID_FILE_DETAIL_BY_NAME = 3;
  private static final int METHODID_SOFTWARE_REGISTER = 4;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final WebImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(WebImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_DEV_REGISTER_CHECK:
          serviceImpl.devRegisterCheck((DevRegisterRequest) request,
              (io.grpc.stub.StreamObserver<DevRegisterReply>) responseObserver);
          break;
        case METHODID_DEV_UN_REGISTER:
          serviceImpl.devUnRegister((DevUnRegisterRequest) request,
              (io.grpc.stub.StreamObserver<DevUnRegisterReply>) responseObserver);
          break;
        case METHODID_FILE_DETAIL_BY_TYPE:
          serviceImpl.fileDetailByType((FileDetailRequestByType) request,
              (io.grpc.stub.StreamObserver<FileDetailRespone>) responseObserver);
          break;
        case METHODID_FILE_DETAIL_BY_NAME:
          serviceImpl.fileDetailByName((FileDetailRequestByName) request,
              (io.grpc.stub.StreamObserver<FileDetailRespone>) responseObserver);
          break;
        case METHODID_SOFTWARE_REGISTER:
          serviceImpl.softwareRegister((UploadRequest) request,
              (io.grpc.stub.StreamObserver<UploadRespond>) responseObserver);
          break;
        default:
          throw new AssertionError();
      }
    }

    @Override
    @SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
        io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        default:
          throw new AssertionError();
      }
    }
  }

  private static abstract class WebBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    WebBaseDescriptorSupplier() {}

    @Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return WebProto.getDescriptor();
    }

    @Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("Web");
    }
  }

  private static final class WebFileDescriptorSupplier
      extends WebBaseDescriptorSupplier {
    WebFileDescriptorSupplier() {}
  }

  private static final class WebMethodDescriptorSupplier
      extends WebBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    WebMethodDescriptorSupplier(String methodName) {
      this.methodName = methodName;
    }

    @Override
    public com.google.protobuf.Descriptors.MethodDescriptor getMethodDescriptor() {
      return getServiceDescriptor().findMethodByName(methodName);
    }
  }

  private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    io.grpc.ServiceDescriptor result = serviceDescriptor;
    if (result == null) {
      synchronized (WebGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new WebFileDescriptorSupplier())
              .addMethod(getDevRegisterCheckMethod())
              .addMethod(getDevUnRegisterMethod())
              .addMethod(getFileDetailByTypeMethod())
              .addMethod(getFileDetailByNameMethod())
              .addMethod(getSoftwareRegisterMethod())
              .build();
        }
      }
    }
    return result;
  }
}

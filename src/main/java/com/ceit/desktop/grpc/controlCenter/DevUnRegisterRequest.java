// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: web.proto

package com.ceit.desktop.grpc.controlCenter;

/**
 * Protobuf type {@code web.DevUnRegisterRequest}
 */
public final class DevUnRegisterRequest extends
    com.google.protobuf.GeneratedMessageV3 implements
    // @@protoc_insertion_point(message_implements:web.DevUnRegisterRequest)
    DevUnRegisterRequestOrBuilder {
private static final long serialVersionUID = 0L;
  // Use DevUnRegisterRequest.newBuilder() to construct.
  private DevUnRegisterRequest(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
    super(builder);
  }
  private DevUnRegisterRequest() {
    username_ = "";
    devicaMac_ = "";
  }

  @Override
  @SuppressWarnings({"unused"})
  protected Object newInstance(
      UnusedPrivateParameter unused) {
    return new DevUnRegisterRequest();
  }

  @Override
  public final com.google.protobuf.UnknownFieldSet
  getUnknownFields() {
    return this.unknownFields;
  }
  private DevUnRegisterRequest(
      com.google.protobuf.CodedInputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    this();
    if (extensionRegistry == null) {
      throw new NullPointerException();
    }
    com.google.protobuf.UnknownFieldSet.Builder unknownFields =
        com.google.protobuf.UnknownFieldSet.newBuilder();
    try {
      boolean done = false;
      while (!done) {
        int tag = input.readTag();
        switch (tag) {
          case 0:
            done = true;
            break;
          case 10: {
            String s = input.readStringRequireUtf8();

            username_ = s;
            break;
          }
          case 18: {
            String s = input.readStringRequireUtf8();

            devicaMac_ = s;
            break;
          }
          default: {
            if (!parseUnknownField(
                input, unknownFields, extensionRegistry, tag)) {
              done = true;
            }
            break;
          }
        }
      }
    } catch (com.google.protobuf.InvalidProtocolBufferException e) {
      throw e.setUnfinishedMessage(this);
    } catch (java.io.IOException e) {
      throw new com.google.protobuf.InvalidProtocolBufferException(
          e).setUnfinishedMessage(this);
    } finally {
      this.unknownFields = unknownFields.build();
      makeExtensionsImmutable();
    }
  }
  public static final com.google.protobuf.Descriptors.Descriptor
      getDescriptor() {
    return WebProto.internal_static_web_DevUnRegisterRequest_descriptor;
  }

  @Override
  protected FieldAccessorTable
      internalGetFieldAccessorTable() {
    return WebProto.internal_static_web_DevUnRegisterRequest_fieldAccessorTable
        .ensureFieldAccessorsInitialized(
            DevUnRegisterRequest.class, Builder.class);
  }

  public static final int USERNAME_FIELD_NUMBER = 1;
  private volatile Object username_;
  /**
   * <code>string username = 1;</code>
   * @return The username.
   */
  @Override
  public String getUsername() {
    Object ref = username_;
    if (ref instanceof String) {
      return (String) ref;
    } else {
      com.google.protobuf.ByteString bs = 
          (com.google.protobuf.ByteString) ref;
      String s = bs.toStringUtf8();
      username_ = s;
      return s;
    }
  }
  /**
   * <code>string username = 1;</code>
   * @return The bytes for username.
   */
  @Override
  public com.google.protobuf.ByteString
      getUsernameBytes() {
    Object ref = username_;
    if (ref instanceof String) {
      com.google.protobuf.ByteString b = 
          com.google.protobuf.ByteString.copyFromUtf8(
              (String) ref);
      username_ = b;
      return b;
    } else {
      return (com.google.protobuf.ByteString) ref;
    }
  }

  public static final int DEVICA_MAC_FIELD_NUMBER = 2;
  private volatile Object devicaMac_;
  /**
   * <code>string devica_mac = 2;</code>
   * @return The devicaMac.
   */
  @Override
  public String getDevicaMac() {
    Object ref = devicaMac_;
    if (ref instanceof String) {
      return (String) ref;
    } else {
      com.google.protobuf.ByteString bs = 
          (com.google.protobuf.ByteString) ref;
      String s = bs.toStringUtf8();
      devicaMac_ = s;
      return s;
    }
  }
  /**
   * <code>string devica_mac = 2;</code>
   * @return The bytes for devicaMac.
   */
  @Override
  public com.google.protobuf.ByteString
      getDevicaMacBytes() {
    Object ref = devicaMac_;
    if (ref instanceof String) {
      com.google.protobuf.ByteString b = 
          com.google.protobuf.ByteString.copyFromUtf8(
              (String) ref);
      devicaMac_ = b;
      return b;
    } else {
      return (com.google.protobuf.ByteString) ref;
    }
  }

  private byte memoizedIsInitialized = -1;
  @Override
  public final boolean isInitialized() {
    byte isInitialized = memoizedIsInitialized;
    if (isInitialized == 1) return true;
    if (isInitialized == 0) return false;

    memoizedIsInitialized = 1;
    return true;
  }

  @Override
  public void writeTo(com.google.protobuf.CodedOutputStream output)
                      throws java.io.IOException {
    if (!getUsernameBytes().isEmpty()) {
      com.google.protobuf.GeneratedMessageV3.writeString(output, 1, username_);
    }
    if (!getDevicaMacBytes().isEmpty()) {
      com.google.protobuf.GeneratedMessageV3.writeString(output, 2, devicaMac_);
    }
    unknownFields.writeTo(output);
  }

  @Override
  public int getSerializedSize() {
    int size = memoizedSize;
    if (size != -1) return size;

    size = 0;
    if (!getUsernameBytes().isEmpty()) {
      size += com.google.protobuf.GeneratedMessageV3.computeStringSize(1, username_);
    }
    if (!getDevicaMacBytes().isEmpty()) {
      size += com.google.protobuf.GeneratedMessageV3.computeStringSize(2, devicaMac_);
    }
    size += unknownFields.getSerializedSize();
    memoizedSize = size;
    return size;
  }

  @Override
  public boolean equals(final Object obj) {
    if (obj == this) {
     return true;
    }
    if (!(obj instanceof DevUnRegisterRequest)) {
      return super.equals(obj);
    }
    DevUnRegisterRequest other = (DevUnRegisterRequest) obj;

    if (!getUsername()
        .equals(other.getUsername())) return false;
    if (!getDevicaMac()
        .equals(other.getDevicaMac())) return false;
    if (!unknownFields.equals(other.unknownFields)) return false;
    return true;
  }

  @Override
  public int hashCode() {
    if (memoizedHashCode != 0) {
      return memoizedHashCode;
    }
    int hash = 41;
    hash = (19 * hash) + getDescriptor().hashCode();
    hash = (37 * hash) + USERNAME_FIELD_NUMBER;
    hash = (53 * hash) + getUsername().hashCode();
    hash = (37 * hash) + DEVICA_MAC_FIELD_NUMBER;
    hash = (53 * hash) + getDevicaMac().hashCode();
    hash = (29 * hash) + unknownFields.hashCode();
    memoizedHashCode = hash;
    return hash;
  }

  public static DevUnRegisterRequest parseFrom(
      java.nio.ByteBuffer data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static DevUnRegisterRequest parseFrom(
      java.nio.ByteBuffer data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static DevUnRegisterRequest parseFrom(
      com.google.protobuf.ByteString data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static DevUnRegisterRequest parseFrom(
      com.google.protobuf.ByteString data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static DevUnRegisterRequest parseFrom(byte[] data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static DevUnRegisterRequest parseFrom(
      byte[] data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static DevUnRegisterRequest parseFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static DevUnRegisterRequest parseFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input, extensionRegistry);
  }
  public static DevUnRegisterRequest parseDelimitedFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input);
  }
  public static DevUnRegisterRequest parseDelimitedFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
  }
  public static DevUnRegisterRequest parseFrom(
      com.google.protobuf.CodedInputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static DevUnRegisterRequest parseFrom(
      com.google.protobuf.CodedInputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input, extensionRegistry);
  }

  @Override
  public Builder newBuilderForType() { return newBuilder(); }
  public static Builder newBuilder() {
    return DEFAULT_INSTANCE.toBuilder();
  }
  public static Builder newBuilder(DevUnRegisterRequest prototype) {
    return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
  }
  @Override
  public Builder toBuilder() {
    return this == DEFAULT_INSTANCE
        ? new Builder() : new Builder().mergeFrom(this);
  }

  @Override
  protected Builder newBuilderForType(
      BuilderParent parent) {
    Builder builder = new Builder(parent);
    return builder;
  }
  /**
   * Protobuf type {@code web.DevUnRegisterRequest}
   */
  public static final class Builder extends
      com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
      // @@protoc_insertion_point(builder_implements:web.DevUnRegisterRequest)
      DevUnRegisterRequestOrBuilder {
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return WebProto.internal_static_web_DevUnRegisterRequest_descriptor;
    }

    @Override
    protected FieldAccessorTable
        internalGetFieldAccessorTable() {
      return WebProto.internal_static_web_DevUnRegisterRequest_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              DevUnRegisterRequest.class, Builder.class);
    }

    // Construct using com.ceit.desktop.grpc.controlCenter.DevUnRegisterRequest.newBuilder()
    private Builder() {
      maybeForceBuilderInitialization();
    }

    private Builder(
        BuilderParent parent) {
      super(parent);
      maybeForceBuilderInitialization();
    }
    private void maybeForceBuilderInitialization() {
      if (com.google.protobuf.GeneratedMessageV3
              .alwaysUseFieldBuilders) {
      }
    }
    @Override
    public Builder clear() {
      super.clear();
      username_ = "";

      devicaMac_ = "";

      return this;
    }

    @Override
    public com.google.protobuf.Descriptors.Descriptor
        getDescriptorForType() {
      return WebProto.internal_static_web_DevUnRegisterRequest_descriptor;
    }

    @Override
    public DevUnRegisterRequest getDefaultInstanceForType() {
      return DevUnRegisterRequest.getDefaultInstance();
    }

    @Override
    public DevUnRegisterRequest build() {
      DevUnRegisterRequest result = buildPartial();
      if (!result.isInitialized()) {
        throw newUninitializedMessageException(result);
      }
      return result;
    }

    @Override
    public DevUnRegisterRequest buildPartial() {
      DevUnRegisterRequest result = new DevUnRegisterRequest(this);
      result.username_ = username_;
      result.devicaMac_ = devicaMac_;
      onBuilt();
      return result;
    }

    @Override
    public Builder clone() {
      return super.clone();
    }
    @Override
    public Builder setField(
        com.google.protobuf.Descriptors.FieldDescriptor field,
        Object value) {
      return super.setField(field, value);
    }
    @Override
    public Builder clearField(
        com.google.protobuf.Descriptors.FieldDescriptor field) {
      return super.clearField(field);
    }
    @Override
    public Builder clearOneof(
        com.google.protobuf.Descriptors.OneofDescriptor oneof) {
      return super.clearOneof(oneof);
    }
    @Override
    public Builder setRepeatedField(
        com.google.protobuf.Descriptors.FieldDescriptor field,
        int index, Object value) {
      return super.setRepeatedField(field, index, value);
    }
    @Override
    public Builder addRepeatedField(
        com.google.protobuf.Descriptors.FieldDescriptor field,
        Object value) {
      return super.addRepeatedField(field, value);
    }
    @Override
    public Builder mergeFrom(com.google.protobuf.Message other) {
      if (other instanceof DevUnRegisterRequest) {
        return mergeFrom((DevUnRegisterRequest)other);
      } else {
        super.mergeFrom(other);
        return this;
      }
    }

    public Builder mergeFrom(DevUnRegisterRequest other) {
      if (other == DevUnRegisterRequest.getDefaultInstance()) return this;
      if (!other.getUsername().isEmpty()) {
        username_ = other.username_;
        onChanged();
      }
      if (!other.getDevicaMac().isEmpty()) {
        devicaMac_ = other.devicaMac_;
        onChanged();
      }
      this.mergeUnknownFields(other.unknownFields);
      onChanged();
      return this;
    }

    @Override
    public final boolean isInitialized() {
      return true;
    }

    @Override
    public Builder mergeFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      DevUnRegisterRequest parsedMessage = null;
      try {
        parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
      } catch (com.google.protobuf.InvalidProtocolBufferException e) {
        parsedMessage = (DevUnRegisterRequest) e.getUnfinishedMessage();
        throw e.unwrapIOException();
      } finally {
        if (parsedMessage != null) {
          mergeFrom(parsedMessage);
        }
      }
      return this;
    }

    private Object username_ = "";
    /**
     * <code>string username = 1;</code>
     * @return The username.
     */
    public String getUsername() {
      Object ref = username_;
      if (!(ref instanceof String)) {
        com.google.protobuf.ByteString bs =
            (com.google.protobuf.ByteString) ref;
        String s = bs.toStringUtf8();
        username_ = s;
        return s;
      } else {
        return (String) ref;
      }
    }
    /**
     * <code>string username = 1;</code>
     * @return The bytes for username.
     */
    public com.google.protobuf.ByteString
        getUsernameBytes() {
      Object ref = username_;
      if (ref instanceof String) {
        com.google.protobuf.ByteString b = 
            com.google.protobuf.ByteString.copyFromUtf8(
                (String) ref);
        username_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }
    /**
     * <code>string username = 1;</code>
     * @param value The username to set.
     * @return This builder for chaining.
     */
    public Builder setUsername(
        String value) {
      if (value == null) {
    throw new NullPointerException();
  }
  
      username_ = value;
      onChanged();
      return this;
    }
    /**
     * <code>string username = 1;</code>
     * @return This builder for chaining.
     */
    public Builder clearUsername() {
      
      username_ = getDefaultInstance().getUsername();
      onChanged();
      return this;
    }
    /**
     * <code>string username = 1;</code>
     * @param value The bytes for username to set.
     * @return This builder for chaining.
     */
    public Builder setUsernameBytes(
        com.google.protobuf.ByteString value) {
      if (value == null) {
    throw new NullPointerException();
  }
  checkByteStringIsUtf8(value);
      
      username_ = value;
      onChanged();
      return this;
    }

    private Object devicaMac_ = "";
    /**
     * <code>string devica_mac = 2;</code>
     * @return The devicaMac.
     */
    public String getDevicaMac() {
      Object ref = devicaMac_;
      if (!(ref instanceof String)) {
        com.google.protobuf.ByteString bs =
            (com.google.protobuf.ByteString) ref;
        String s = bs.toStringUtf8();
        devicaMac_ = s;
        return s;
      } else {
        return (String) ref;
      }
    }
    /**
     * <code>string devica_mac = 2;</code>
     * @return The bytes for devicaMac.
     */
    public com.google.protobuf.ByteString
        getDevicaMacBytes() {
      Object ref = devicaMac_;
      if (ref instanceof String) {
        com.google.protobuf.ByteString b = 
            com.google.protobuf.ByteString.copyFromUtf8(
                (String) ref);
        devicaMac_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }
    /**
     * <code>string devica_mac = 2;</code>
     * @param value The devicaMac to set.
     * @return This builder for chaining.
     */
    public Builder setDevicaMac(
        String value) {
      if (value == null) {
    throw new NullPointerException();
  }
  
      devicaMac_ = value;
      onChanged();
      return this;
    }
    /**
     * <code>string devica_mac = 2;</code>
     * @return This builder for chaining.
     */
    public Builder clearDevicaMac() {
      
      devicaMac_ = getDefaultInstance().getDevicaMac();
      onChanged();
      return this;
    }
    /**
     * <code>string devica_mac = 2;</code>
     * @param value The bytes for devicaMac to set.
     * @return This builder for chaining.
     */
    public Builder setDevicaMacBytes(
        com.google.protobuf.ByteString value) {
      if (value == null) {
    throw new NullPointerException();
  }
  checkByteStringIsUtf8(value);
      
      devicaMac_ = value;
      onChanged();
      return this;
    }
    @Override
    public final Builder setUnknownFields(
        final com.google.protobuf.UnknownFieldSet unknownFields) {
      return super.setUnknownFields(unknownFields);
    }

    @Override
    public final Builder mergeUnknownFields(
        final com.google.protobuf.UnknownFieldSet unknownFields) {
      return super.mergeUnknownFields(unknownFields);
    }


    // @@protoc_insertion_point(builder_scope:web.DevUnRegisterRequest)
  }

  // @@protoc_insertion_point(class_scope:web.DevUnRegisterRequest)
  private static final DevUnRegisterRequest DEFAULT_INSTANCE;
  static {
    DEFAULT_INSTANCE = new DevUnRegisterRequest();
  }

  public static DevUnRegisterRequest getDefaultInstance() {
    return DEFAULT_INSTANCE;
  }

  private static final com.google.protobuf.Parser<DevUnRegisterRequest>
      PARSER = new com.google.protobuf.AbstractParser<DevUnRegisterRequest>() {
    @Override
    public DevUnRegisterRequest parsePartialFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return new DevUnRegisterRequest(input, extensionRegistry);
    }
  };

  public static com.google.protobuf.Parser<DevUnRegisterRequest> parser() {
    return PARSER;
  }

  @Override
  public com.google.protobuf.Parser<DevUnRegisterRequest> getParserForType() {
    return PARSER;
  }

  @Override
  public DevUnRegisterRequest getDefaultInstanceForType() {
    return DEFAULT_INSTANCE;
  }

}


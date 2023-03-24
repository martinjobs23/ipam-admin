// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: web.proto

package com.ceit.desktop.grpc.controlCenter;

/**
 * Protobuf type {@code web.DevRegisterRequest}
 */
public final class DevRegisterRequest extends
    com.google.protobuf.GeneratedMessageV3 implements
    // @@protoc_insertion_point(message_implements:web.DevRegisterRequest)
    DevRegisterRequestOrBuilder {
private static final long serialVersionUID = 0L;
  // Use DevRegisterRequest.newBuilder() to construct.
  private DevRegisterRequest(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
    super(builder);
  }
  private DevRegisterRequest() {
    serial_ = "";
    devName_ = "";
    orgId_ = "";
    deviceIp_ = "";
  }

  @Override
  @SuppressWarnings({"unused"})
  protected Object newInstance(
      UnusedPrivateParameter unused) {
    return new DevRegisterRequest();
  }

  @Override
  public final com.google.protobuf.UnknownFieldSet
  getUnknownFields() {
    return this.unknownFields;
  }
  private DevRegisterRequest(
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
          case 8: {

            isHandle_ = input.readInt32();
            break;
          }
          case 18: {
            String s = input.readStringRequireUtf8();

            serial_ = s;
            break;
          }
          case 26: {
            String s = input.readStringRequireUtf8();

            devName_ = s;
            break;
          }
          case 34: {
            String s = input.readStringRequireUtf8();

            orgId_ = s;
            break;
          }
          case 42: {
            String s = input.readStringRequireUtf8();

            deviceIp_ = s;
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
    return WebProto.internal_static_web_DevRegisterRequest_descriptor;
  }

  @Override
  protected FieldAccessorTable
      internalGetFieldAccessorTable() {
    return WebProto.internal_static_web_DevRegisterRequest_fieldAccessorTable
        .ensureFieldAccessorsInitialized(
            DevRegisterRequest.class, Builder.class);
  }

  public static final int IS_HANDLE_FIELD_NUMBER = 1;
  private int isHandle_;
  /**
   * <code>int32 is_handle = 1;</code>
   * @return The isHandle.
   */
  @Override
  public int getIsHandle() {
    return isHandle_;
  }

  public static final int SERIAL_FIELD_NUMBER = 2;
  private volatile Object serial_;
  /**
   * <code>string serial = 2;</code>
   * @return The serial.
   */
  @Override
  public String getSerial() {
    Object ref = serial_;
    if (ref instanceof String) {
      return (String) ref;
    } else {
      com.google.protobuf.ByteString bs = 
          (com.google.protobuf.ByteString) ref;
      String s = bs.toStringUtf8();
      serial_ = s;
      return s;
    }
  }
  /**
   * <code>string serial = 2;</code>
   * @return The bytes for serial.
   */
  @Override
  public com.google.protobuf.ByteString
      getSerialBytes() {
    Object ref = serial_;
    if (ref instanceof String) {
      com.google.protobuf.ByteString b = 
          com.google.protobuf.ByteString.copyFromUtf8(
              (String) ref);
      serial_ = b;
      return b;
    } else {
      return (com.google.protobuf.ByteString) ref;
    }
  }

  public static final int DEV_NAME_FIELD_NUMBER = 3;
  private volatile Object devName_;
  /**
   * <code>string dev_name = 3;</code>
   * @return The devName.
   */
  @Override
  public String getDevName() {
    Object ref = devName_;
    if (ref instanceof String) {
      return (String) ref;
    } else {
      com.google.protobuf.ByteString bs = 
          (com.google.protobuf.ByteString) ref;
      String s = bs.toStringUtf8();
      devName_ = s;
      return s;
    }
  }
  /**
   * <code>string dev_name = 3;</code>
   * @return The bytes for devName.
   */
  @Override
  public com.google.protobuf.ByteString
      getDevNameBytes() {
    Object ref = devName_;
    if (ref instanceof String) {
      com.google.protobuf.ByteString b = 
          com.google.protobuf.ByteString.copyFromUtf8(
              (String) ref);
      devName_ = b;
      return b;
    } else {
      return (com.google.protobuf.ByteString) ref;
    }
  }

  public static final int ORG_ID_FIELD_NUMBER = 4;
  private volatile Object orgId_;
  /**
   * <code>string org_id = 4;</code>
   * @return The orgId.
   */
  @Override
  public String getOrgId() {
    Object ref = orgId_;
    if (ref instanceof String) {
      return (String) ref;
    } else {
      com.google.protobuf.ByteString bs = 
          (com.google.protobuf.ByteString) ref;
      String s = bs.toStringUtf8();
      orgId_ = s;
      return s;
    }
  }
  /**
   * <code>string org_id = 4;</code>
   * @return The bytes for orgId.
   */
  @Override
  public com.google.protobuf.ByteString
      getOrgIdBytes() {
    Object ref = orgId_;
    if (ref instanceof String) {
      com.google.protobuf.ByteString b = 
          com.google.protobuf.ByteString.copyFromUtf8(
              (String) ref);
      orgId_ = b;
      return b;
    } else {
      return (com.google.protobuf.ByteString) ref;
    }
  }

  public static final int DEVICE_IP_FIELD_NUMBER = 5;
  private volatile Object deviceIp_;
  /**
   * <code>string device_ip = 5;</code>
   * @return The deviceIp.
   */
  @Override
  public String getDeviceIp() {
    Object ref = deviceIp_;
    if (ref instanceof String) {
      return (String) ref;
    } else {
      com.google.protobuf.ByteString bs = 
          (com.google.protobuf.ByteString) ref;
      String s = bs.toStringUtf8();
      deviceIp_ = s;
      return s;
    }
  }
  /**
   * <code>string device_ip = 5;</code>
   * @return The bytes for deviceIp.
   */
  @Override
  public com.google.protobuf.ByteString
      getDeviceIpBytes() {
    Object ref = deviceIp_;
    if (ref instanceof String) {
      com.google.protobuf.ByteString b = 
          com.google.protobuf.ByteString.copyFromUtf8(
              (String) ref);
      deviceIp_ = b;
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
    if (isHandle_ != 0) {
      output.writeInt32(1, isHandle_);
    }
    if (!getSerialBytes().isEmpty()) {
      com.google.protobuf.GeneratedMessageV3.writeString(output, 2, serial_);
    }
    if (!getDevNameBytes().isEmpty()) {
      com.google.protobuf.GeneratedMessageV3.writeString(output, 3, devName_);
    }
    if (!getOrgIdBytes().isEmpty()) {
      com.google.protobuf.GeneratedMessageV3.writeString(output, 4, orgId_);
    }
    if (!getDeviceIpBytes().isEmpty()) {
      com.google.protobuf.GeneratedMessageV3.writeString(output, 5, deviceIp_);
    }
    unknownFields.writeTo(output);
  }

  @Override
  public int getSerializedSize() {
    int size = memoizedSize;
    if (size != -1) return size;

    size = 0;
    if (isHandle_ != 0) {
      size += com.google.protobuf.CodedOutputStream
        .computeInt32Size(1, isHandle_);
    }
    if (!getSerialBytes().isEmpty()) {
      size += com.google.protobuf.GeneratedMessageV3.computeStringSize(2, serial_);
    }
    if (!getDevNameBytes().isEmpty()) {
      size += com.google.protobuf.GeneratedMessageV3.computeStringSize(3, devName_);
    }
    if (!getOrgIdBytes().isEmpty()) {
      size += com.google.protobuf.GeneratedMessageV3.computeStringSize(4, orgId_);
    }
    if (!getDeviceIpBytes().isEmpty()) {
      size += com.google.protobuf.GeneratedMessageV3.computeStringSize(5, deviceIp_);
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
    if (!(obj instanceof DevRegisterRequest)) {
      return super.equals(obj);
    }
    DevRegisterRequest other = (DevRegisterRequest) obj;

    if (getIsHandle()
        != other.getIsHandle()) return false;
    if (!getSerial()
        .equals(other.getSerial())) return false;
    if (!getDevName()
        .equals(other.getDevName())) return false;
    if (!getOrgId()
        .equals(other.getOrgId())) return false;
    if (!getDeviceIp()
        .equals(other.getDeviceIp())) return false;
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
    hash = (37 * hash) + IS_HANDLE_FIELD_NUMBER;
    hash = (53 * hash) + getIsHandle();
    hash = (37 * hash) + SERIAL_FIELD_NUMBER;
    hash = (53 * hash) + getSerial().hashCode();
    hash = (37 * hash) + DEV_NAME_FIELD_NUMBER;
    hash = (53 * hash) + getDevName().hashCode();
    hash = (37 * hash) + ORG_ID_FIELD_NUMBER;
    hash = (53 * hash) + getOrgId().hashCode();
    hash = (37 * hash) + DEVICE_IP_FIELD_NUMBER;
    hash = (53 * hash) + getDeviceIp().hashCode();
    hash = (29 * hash) + unknownFields.hashCode();
    memoizedHashCode = hash;
    return hash;
  }

  public static DevRegisterRequest parseFrom(
      java.nio.ByteBuffer data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static DevRegisterRequest parseFrom(
      java.nio.ByteBuffer data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static DevRegisterRequest parseFrom(
      com.google.protobuf.ByteString data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static DevRegisterRequest parseFrom(
      com.google.protobuf.ByteString data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static DevRegisterRequest parseFrom(byte[] data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static DevRegisterRequest parseFrom(
      byte[] data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static DevRegisterRequest parseFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static DevRegisterRequest parseFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input, extensionRegistry);
  }
  public static DevRegisterRequest parseDelimitedFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input);
  }
  public static DevRegisterRequest parseDelimitedFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
  }
  public static DevRegisterRequest parseFrom(
      com.google.protobuf.CodedInputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static DevRegisterRequest parseFrom(
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
  public static Builder newBuilder(DevRegisterRequest prototype) {
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
   * Protobuf type {@code web.DevRegisterRequest}
   */
  public static final class Builder extends
      com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
      // @@protoc_insertion_point(builder_implements:web.DevRegisterRequest)
      DevRegisterRequestOrBuilder {
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return WebProto.internal_static_web_DevRegisterRequest_descriptor;
    }

    @Override
    protected FieldAccessorTable
        internalGetFieldAccessorTable() {
      return WebProto.internal_static_web_DevRegisterRequest_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              DevRegisterRequest.class, Builder.class);
    }

    // Construct using com.ceit.desktop.grpc.controlCenter.DevRegisterRequest.newBuilder()
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
      isHandle_ = 0;

      serial_ = "";

      devName_ = "";

      orgId_ = "";

      deviceIp_ = "";

      return this;
    }

    @Override
    public com.google.protobuf.Descriptors.Descriptor
        getDescriptorForType() {
      return WebProto.internal_static_web_DevRegisterRequest_descriptor;
    }

    @Override
    public DevRegisterRequest getDefaultInstanceForType() {
      return DevRegisterRequest.getDefaultInstance();
    }

    @Override
    public DevRegisterRequest build() {
      DevRegisterRequest result = buildPartial();
      if (!result.isInitialized()) {
        throw newUninitializedMessageException(result);
      }
      return result;
    }

    @Override
    public DevRegisterRequest buildPartial() {
      DevRegisterRequest result = new DevRegisterRequest(this);
      result.isHandle_ = isHandle_;
      result.serial_ = serial_;
      result.devName_ = devName_;
      result.orgId_ = orgId_;
      result.deviceIp_ = deviceIp_;
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
      if (other instanceof DevRegisterRequest) {
        return mergeFrom((DevRegisterRequest)other);
      } else {
        super.mergeFrom(other);
        return this;
      }
    }

    public Builder mergeFrom(DevRegisterRequest other) {
      if (other == DevRegisterRequest.getDefaultInstance()) return this;
      if (other.getIsHandle() != 0) {
        setIsHandle(other.getIsHandle());
      }
      if (!other.getSerial().isEmpty()) {
        serial_ = other.serial_;
        onChanged();
      }
      if (!other.getDevName().isEmpty()) {
        devName_ = other.devName_;
        onChanged();
      }
      if (!other.getOrgId().isEmpty()) {
        orgId_ = other.orgId_;
        onChanged();
      }
      if (!other.getDeviceIp().isEmpty()) {
        deviceIp_ = other.deviceIp_;
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
      DevRegisterRequest parsedMessage = null;
      try {
        parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
      } catch (com.google.protobuf.InvalidProtocolBufferException e) {
        parsedMessage = (DevRegisterRequest) e.getUnfinishedMessage();
        throw e.unwrapIOException();
      } finally {
        if (parsedMessage != null) {
          mergeFrom(parsedMessage);
        }
      }
      return this;
    }

    private int isHandle_ ;
    /**
     * <code>int32 is_handle = 1;</code>
     * @return The isHandle.
     */
    @Override
    public int getIsHandle() {
      return isHandle_;
    }
    /**
     * <code>int32 is_handle = 1;</code>
     * @param value The isHandle to set.
     * @return This builder for chaining.
     */
    public Builder setIsHandle(int value) {
      
      isHandle_ = value;
      onChanged();
      return this;
    }
    /**
     * <code>int32 is_handle = 1;</code>
     * @return This builder for chaining.
     */
    public Builder clearIsHandle() {
      
      isHandle_ = 0;
      onChanged();
      return this;
    }

    private Object serial_ = "";
    /**
     * <code>string serial = 2;</code>
     * @return The serial.
     */
    public String getSerial() {
      Object ref = serial_;
      if (!(ref instanceof String)) {
        com.google.protobuf.ByteString bs =
            (com.google.protobuf.ByteString) ref;
        String s = bs.toStringUtf8();
        serial_ = s;
        return s;
      } else {
        return (String) ref;
      }
    }
    /**
     * <code>string serial = 2;</code>
     * @return The bytes for serial.
     */
    public com.google.protobuf.ByteString
        getSerialBytes() {
      Object ref = serial_;
      if (ref instanceof String) {
        com.google.protobuf.ByteString b = 
            com.google.protobuf.ByteString.copyFromUtf8(
                (String) ref);
        serial_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }
    /**
     * <code>string serial = 2;</code>
     * @param value The serial to set.
     * @return This builder for chaining.
     */
    public Builder setSerial(
        String value) {
      if (value == null) {
    throw new NullPointerException();
  }
  
      serial_ = value;
      onChanged();
      return this;
    }
    /**
     * <code>string serial = 2;</code>
     * @return This builder for chaining.
     */
    public Builder clearSerial() {
      
      serial_ = getDefaultInstance().getSerial();
      onChanged();
      return this;
    }
    /**
     * <code>string serial = 2;</code>
     * @param value The bytes for serial to set.
     * @return This builder for chaining.
     */
    public Builder setSerialBytes(
        com.google.protobuf.ByteString value) {
      if (value == null) {
    throw new NullPointerException();
  }
  checkByteStringIsUtf8(value);
      
      serial_ = value;
      onChanged();
      return this;
    }

    private Object devName_ = "";
    /**
     * <code>string dev_name = 3;</code>
     * @return The devName.
     */
    public String getDevName() {
      Object ref = devName_;
      if (!(ref instanceof String)) {
        com.google.protobuf.ByteString bs =
            (com.google.protobuf.ByteString) ref;
        String s = bs.toStringUtf8();
        devName_ = s;
        return s;
      } else {
        return (String) ref;
      }
    }
    /**
     * <code>string dev_name = 3;</code>
     * @return The bytes for devName.
     */
    public com.google.protobuf.ByteString
        getDevNameBytes() {
      Object ref = devName_;
      if (ref instanceof String) {
        com.google.protobuf.ByteString b = 
            com.google.protobuf.ByteString.copyFromUtf8(
                (String) ref);
        devName_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }
    /**
     * <code>string dev_name = 3;</code>
     * @param value The devName to set.
     * @return This builder for chaining.
     */
    public Builder setDevName(
        String value) {
      if (value == null) {
    throw new NullPointerException();
  }
  
      devName_ = value;
      onChanged();
      return this;
    }
    /**
     * <code>string dev_name = 3;</code>
     * @return This builder for chaining.
     */
    public Builder clearDevName() {
      
      devName_ = getDefaultInstance().getDevName();
      onChanged();
      return this;
    }
    /**
     * <code>string dev_name = 3;</code>
     * @param value The bytes for devName to set.
     * @return This builder for chaining.
     */
    public Builder setDevNameBytes(
        com.google.protobuf.ByteString value) {
      if (value == null) {
    throw new NullPointerException();
  }
  checkByteStringIsUtf8(value);
      
      devName_ = value;
      onChanged();
      return this;
    }

    private Object orgId_ = "";
    /**
     * <code>string org_id = 4;</code>
     * @return The orgId.
     */
    public String getOrgId() {
      Object ref = orgId_;
      if (!(ref instanceof String)) {
        com.google.protobuf.ByteString bs =
            (com.google.protobuf.ByteString) ref;
        String s = bs.toStringUtf8();
        orgId_ = s;
        return s;
      } else {
        return (String) ref;
      }
    }
    /**
     * <code>string org_id = 4;</code>
     * @return The bytes for orgId.
     */
    public com.google.protobuf.ByteString
        getOrgIdBytes() {
      Object ref = orgId_;
      if (ref instanceof String) {
        com.google.protobuf.ByteString b = 
            com.google.protobuf.ByteString.copyFromUtf8(
                (String) ref);
        orgId_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }
    /**
     * <code>string org_id = 4;</code>
     * @param value The orgId to set.
     * @return This builder for chaining.
     */
    public Builder setOrgId(
        String value) {
      if (value == null) {
    throw new NullPointerException();
  }
  
      orgId_ = value;
      onChanged();
      return this;
    }
    /**
     * <code>string org_id = 4;</code>
     * @return This builder for chaining.
     */
    public Builder clearOrgId() {
      
      orgId_ = getDefaultInstance().getOrgId();
      onChanged();
      return this;
    }
    /**
     * <code>string org_id = 4;</code>
     * @param value The bytes for orgId to set.
     * @return This builder for chaining.
     */
    public Builder setOrgIdBytes(
        com.google.protobuf.ByteString value) {
      if (value == null) {
    throw new NullPointerException();
  }
  checkByteStringIsUtf8(value);
      
      orgId_ = value;
      onChanged();
      return this;
    }

    private Object deviceIp_ = "";
    /**
     * <code>string device_ip = 5;</code>
     * @return The deviceIp.
     */
    public String getDeviceIp() {
      Object ref = deviceIp_;
      if (!(ref instanceof String)) {
        com.google.protobuf.ByteString bs =
            (com.google.protobuf.ByteString) ref;
        String s = bs.toStringUtf8();
        deviceIp_ = s;
        return s;
      } else {
        return (String) ref;
      }
    }
    /**
     * <code>string device_ip = 5;</code>
     * @return The bytes for deviceIp.
     */
    public com.google.protobuf.ByteString
        getDeviceIpBytes() {
      Object ref = deviceIp_;
      if (ref instanceof String) {
        com.google.protobuf.ByteString b = 
            com.google.protobuf.ByteString.copyFromUtf8(
                (String) ref);
        deviceIp_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }
    /**
     * <code>string device_ip = 5;</code>
     * @param value The deviceIp to set.
     * @return This builder for chaining.
     */
    public Builder setDeviceIp(
        String value) {
      if (value == null) {
    throw new NullPointerException();
  }
  
      deviceIp_ = value;
      onChanged();
      return this;
    }
    /**
     * <code>string device_ip = 5;</code>
     * @return This builder for chaining.
     */
    public Builder clearDeviceIp() {
      
      deviceIp_ = getDefaultInstance().getDeviceIp();
      onChanged();
      return this;
    }
    /**
     * <code>string device_ip = 5;</code>
     * @param value The bytes for deviceIp to set.
     * @return This builder for chaining.
     */
    public Builder setDeviceIpBytes(
        com.google.protobuf.ByteString value) {
      if (value == null) {
    throw new NullPointerException();
  }
  checkByteStringIsUtf8(value);
      
      deviceIp_ = value;
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


    // @@protoc_insertion_point(builder_scope:web.DevRegisterRequest)
  }

  // @@protoc_insertion_point(class_scope:web.DevRegisterRequest)
  private static final DevRegisterRequest DEFAULT_INSTANCE;
  static {
    DEFAULT_INSTANCE = new DevRegisterRequest();
  }

  public static DevRegisterRequest getDefaultInstance() {
    return DEFAULT_INSTANCE;
  }

  private static final com.google.protobuf.Parser<DevRegisterRequest>
      PARSER = new com.google.protobuf.AbstractParser<DevRegisterRequest>() {
    @Override
    public DevRegisterRequest parsePartialFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return new DevRegisterRequest(input, extensionRegistry);
    }
  };

  public static com.google.protobuf.Parser<DevRegisterRequest> parser() {
    return PARSER;
  }

  @Override
  public com.google.protobuf.Parser<DevRegisterRequest> getParserForType() {
    return PARSER;
  }

  @Override
  public DevRegisterRequest getDefaultInstanceForType() {
    return DEFAULT_INSTANCE;
  }

}


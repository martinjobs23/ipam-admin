// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: web.proto

package com.ceit.desktop.grpc.controlCenter;

public final class WebProto {
  private WebProto() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistryLite registry) {
  }

  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
    registerAllExtensions(
        (com.google.protobuf.ExtensionRegistryLite) registry);
  }
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_web_DevRegisterRequest_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_web_DevRegisterRequest_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_web_DevRegisterReply_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_web_DevRegisterReply_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_web_DevUnRegisterRequest_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_web_DevUnRegisterRequest_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_web_DevUnRegisterReply_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_web_DevUnRegisterReply_fieldAccessorTable;

  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static  com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    String[] descriptorData = {
      "\n\tweb.proto\022\003web\"l\n\022DevRegisterRequest\022\021" +
      "\n\tis_handle\030\001 \001(\005\022\016\n\006serial\030\002 \001(\t\022\020\n\010dev" +
      "_name\030\003 \001(\t\022\016\n\006org_id\030\004 \001(\t\022\021\n\tdevice_ip" +
      "\030\005 \001(\t\";\n\020DevRegisterReply\022\013\n\003msg\030\001 \001(\t\022" +
      "\014\n\004code\030\002 \001(\005\022\014\n\004data\030\003 \001(\t\"<\n\024DevUnRegi" +
      "sterRequest\022\020\n\010username\030\001 \001(\t\022\022\n\ndevica_" +
      "mac\030\002 \001(\t\"=\n\022DevUnRegisterReply\022\013\n\003msg\030\001" +
      " \001(\t\022\014\n\004code\030\002 \001(\005\022\014\n\004data\030\003 \001(\t2\222\001\n\003Web" +
      "\022D\n\020DevRegisterCheck\022\027.web.DevRegisterRe" +
      "quest\032\025.web.DevRegisterReply\"\000\022E\n\rDevUnR" +
      "egister\022\031.web.DevUnRegisterRequest\032\027.web" +
      ".DevUnRegisterReply\"\000B7\n#com.ceit.deskto" +
      "p.grpc.controlCenterB\010WebProtoP\001\242\002\003HLWb\006" +
      "proto3"
    };
    descriptor = com.google.protobuf.Descriptors.FileDescriptor
      .internalBuildGeneratedFileFrom(descriptorData,
        new com.google.protobuf.Descriptors.FileDescriptor[] {
        });
    internal_static_web_DevRegisterRequest_descriptor =
      getDescriptor().getMessageTypes().get(0);
    internal_static_web_DevRegisterRequest_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_web_DevRegisterRequest_descriptor,
        new String[] { "IsHandle", "Serial", "DevName", "OrgId", "DeviceIp", });
    internal_static_web_DevRegisterReply_descriptor =
      getDescriptor().getMessageTypes().get(1);
    internal_static_web_DevRegisterReply_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_web_DevRegisterReply_descriptor,
        new String[] { "Msg", "Code", "Data", });
    internal_static_web_DevUnRegisterRequest_descriptor =
      getDescriptor().getMessageTypes().get(2);
    internal_static_web_DevUnRegisterRequest_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_web_DevUnRegisterRequest_descriptor,
        new String[] { "Username", "DevicaMac", });
    internal_static_web_DevUnRegisterReply_descriptor =
      getDescriptor().getMessageTypes().get(3);
    internal_static_web_DevUnRegisterReply_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_web_DevUnRegisterReply_descriptor,
        new String[] { "Msg", "Code", "Data", });
  }

  // @@protoc_insertion_point(outer_class_scope)
}

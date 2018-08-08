package com.example.ledwisdom1.mesh;

import java.io.File;

public class ReportMesh {
  public  String meshPassword;

  public  String homeName;

  public  String meshName;

  public  String othersId;

  public String imageUrl;

  public int deviceCount;

  public File homeIcon ;


  public ReportMesh() {
  }

  public ReportMesh(String meshPassword, String homeName, String meshName,
                    String othersId, File homeIcon) {
    this.meshPassword = meshPassword;
    this.homeName = homeName;
    this.meshName = meshName;
    this.othersId = othersId;
    this.homeIcon = homeIcon;

  }

  @Override
  public String toString() {
    return "ReportMesh{" +
            "meshPassword='" + meshPassword + '\'' +
            ", homeName='" + homeName + '\'' +
            ", meshName='" + meshName + '\'' +
            ", othersId='" + othersId + '\'' +
            ", homeIcon=" + homeIcon +
            '}';
  }
}

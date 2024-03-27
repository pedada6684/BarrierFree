package com.fullship.hBAF.global.api.response;

import java.util.ArrayList;
import java.util.List;

public class SimplifyGeoCode {

  /**
   * 수직거리를 이용하는 방식
   * @param geoCode 좌표 리스트
   * @param tolerance 허용 거리
   * @return 압축된 좌표
   */
  public static List<String[]> polyLineSimplify(List<String[]> geoCode, double tolerance) {
    List<String[]> compressCode = new ArrayList<>();

    for (int i = 0; i < geoCode.size() - 2; i++) {
      /* 좌표 중복 제거 */
      while (i < geoCode.size() - 2
          && geoCode.get(i)[0].equals(geoCode.get(i + 1)[0])
          && geoCode.get(i)[1].equals(geoCode.get(i + 1)[1])) {
        geoCode.remove(i + 1);
      }
    }

    compressCode.add(geoCode.get(0));
    simplifyPathRecur(geoCode, 0, geoCode.size() - 1, tolerance, compressCode);
    compressCode.add(geoCode.get(geoCode.size() - 1));

    return compressCode;
  }

  private static void simplifyPathRecur(List<String[]> geoCode, int start, int end,
      double tolerance, List<String[]> compressCode) {
    double maxDist = 0;
    int idxFar = 0;

    for (int i = start + 1; i < end; i++) {
      double dist = perpendicularDistance(geoCode.get(i), geoCode.get(start), geoCode.get(end));
      if (dist > maxDist) {
        maxDist = dist;
        idxFar = i;
      }
    }

    if (maxDist > tolerance) {
      if (start < idxFar) {
        simplifyPathRecur(geoCode, start, idxFar, tolerance, compressCode);
      }
      compressCode.add(geoCode.get(idxFar));
      if (idxFar < end - 1) {
        simplifyPathRecur(geoCode, idxFar, end, tolerance, compressCode);
      }
    }
  }

  private static double perpendicularDistance(String[] point, String[] start, String[] end) {
    double pointX = Double.parseDouble(point[0]);
    double pointY = Double.parseDouble(point[1]);
    double startX = Double.parseDouble(start[0]);
    double startY = Double.parseDouble(start[1]);
    double endX = Double.parseDouble(end[0]);
    double endY = Double.parseDouble(end[1]);

    double A = pointX - startX;
    double B = pointY - startY;
    double C = endX - startX;
    double D = endY - startY;

    double dot = A * C + B * D;
    double len_sq = C * C + D * D;
    double param = -1;
    if (len_sq != 0) {
      param = dot / len_sq;
    }

    double xx, yy;

    if (param < 0) {
      xx = startX;
      yy = startY;
    } else if (param > 1) {
      xx = endX;
      yy = endY;
    } else {
      xx = startX + param * C;
      yy = startY + param * D;
    }

    double dx = pointX - xx;
    double dy = pointY - yy;
    return Math.sqrt(dx * dx + dy * dy);
  }

  /**
   * 각도(arctan를 이용하는 방식)
   * @param geoCode 좌표 리스트
   * @param limit 허용 각도
   * @return 압축된 좌표
   */
  public static List<String[]> getCompress(List<String[]> geoCode, double limit) {
    List<String[]> compressCode = new ArrayList<>();
    compressCode.add(geoCode.get(0));

    double prevDegree = Double.MIN_VALUE;
    for (int i = 0; i < geoCode.size() - 1; i++) {
      /* 좌표 중복 제거 */
      while (i < geoCode.size() - 2
          && geoCode.get(i)[0].equals(geoCode.get(i + 1)[0])
          && geoCode.get(i)[1].equals(geoCode.get(i + 1)[1])) {
        geoCode.remove(i + 1);
      }

      double relativeX =
          Double.parseDouble(geoCode.get(i + 1)[0]) - Double.parseDouble(geoCode.get(i)[0]);
      double relativeY =
          Double.parseDouble(geoCode.get(i + 1)[1]) - Double.parseDouble(geoCode.get(i)[1]);

      double radian = Math.atan2(relativeY, relativeX);
      double degree = radian * 180 / Math.PI;
      /* 시작점 확인 */
      if (prevDegree == Double.MIN_VALUE) {
        prevDegree = degree;
        continue;
      }

      if (overRange(degree, prevDegree, limit)) {
        compressCode.add(new String[]{geoCode.get(i)[0], geoCode.get(i)[1]});
        prevDegree = degree;
      }
    }

    compressCode.add(
        (new String[]{geoCode.get(geoCode.size() - 1)[0], geoCode.get(geoCode.size() - 1)[1]}));

    return compressCode;
  }

  private static boolean overRange(double degree, double prevDegree, double limit) {
    double fullAngle = 360;
    double maxDegree = prevDegree + (limit / 2);
    double minDegree = prevDegree - (limit / 2);
    double newDegree = degree;

    if (maxDegree >= (fullAngle / 2) && newDegree < 0) {
      newDegree += fullAngle;
    } else if (minDegree <= -(fullAngle / 2) && newDegree > 0) {
      maxDegree += fullAngle;
      minDegree += fullAngle;
    }

    return minDegree > newDegree || newDegree > maxDegree;
  }
}

package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 地理坐标点类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Point {
    private double longitude; // 经度
    private double latitude;  // 纬度

    /**
     * 获取坐标字符串表示（经度,纬度）
     */
    public String getCoordinateString() {
        return longitude + "," + latitude;
    }

    /**
     * 验证坐标是否有效
     */
    public boolean isValid() {
        return latitude >= -90 && latitude <= 90 &&
                longitude >= -180 && longitude <= 180;
    }

    /**
     * 计算与另一点的距离（简单欧式距离，非地理距离）
     */
    public double distanceTo(Point other) {
        if (other == null) return Double.NaN;
        double dx = this.longitude - other.longitude;
        double dy = this.latitude - other.latitude;
        return Math.sqrt(dx * dx + dy * dy);
    }
}
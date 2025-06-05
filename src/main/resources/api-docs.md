# 动态路由 API 说明文档

## 基本信息

- 基础路径: `/api/v1`
- 响应格式: JSON
- 编码方式: UTF-8

## 接口说明

### 1. 获取路由信息

#### 请求信息

- 请求方法: GET
- 请求路径: `/routes`
- 请求参数:
  
  | 参数名 | 类型 | 必填 | 说明 |
  |--------|------|------|------|
  | startPoint | String | 是 | 起点坐标（格式：经度,纬度） |
  | endPoint | String | 是 | 终点坐标（格式：经度,纬度） |
  | type | Integer | 否 | 路线类型（1: 最快路线, 2: 最短路线, 3: 经济路线），默认为1 |

#### 响应信息

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "routeId": "string",
    "distance": "number",  // 总距离，单位：米
    "duration": "number",  // 预计时间，单位：秒
    "toll": "number",     // 预计费用，单位：元
    "points": [           // 路径点数组
      {
        "lng": "number",  // 经度
        "lat": "number", // 纬度
        "type": "number" // 点类型（1: 起点, 2: 途经点, 3: 终点）
      }
    ],
    "steps": [           // 导航步骤
      {
        "instruction": "string", // 导航指示
        "distance": "number",    // 该段距离
        "duration": "number"     // 该段预计时间
      }
    ]
  }
}
```

### 2. 更新路由

#### 请求信息

- 请求方法: PUT
- 请求路径: `/routes/{routeId}`
- 请求参数:

  | 参数名 | 类型 | 必填 | 说明 |
  |--------|------|------|------|
  | routeId | String | 是 | 路由ID |
  | waypoints | Array | 否 | 途经点坐标数组 |
  | avoidRoads | Array | 否 | 避开的道路名称数组 |

#### 响应信息

```json
{
  "code": 200,
  "message": "success",
  "data": {
    // 与获取路由信息接口返回格式相同
  }
}
```

### 3. 获取实时路况

#### 请求信息

- 请求方法: GET
- 请求路径: `/routes/{routeId}/traffic`
- 请求参数:

  | 参数名 | 类型 | 必填 | 说明 |
  |--------|------|------|------|
  | routeId | String | 是 | 路由ID |

#### 响应信息

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "updateTime": "string",    // 更新时间
    "congestionLevel": "number", // 拥堵等级（1-5）
    "segments": [              // 路段交通状况
      {
        "startIndex": "number", // 起始点索引
        "endIndex": "number",   // 结束点索引
        "status": "number",     // 路况状态（1: 畅通, 2: 缓行, 3: 拥堵, 4: 严重拥堵）
        "speed": "number"       // 平均速度
      }
    ]
  }
}
```

## 错误码说明

| 错误码 | 说明 |
|--------|------|
| 200 | 成功 |
| 400 | 请求参数错误 |
| 401 | 未授权 |
| 403 | 禁止访问 |
| 404 | 资源不存在 |
| 500 | 服务器内部错误 |

## 注意事项

1. 所有坐标点均使用WGS84坐标系
2. 请求频率限制：每个IP每分钟不超过100次
3. 建议在请求头中添加适当的超时设置
4. 返回的距离单位均为米，时间单位为秒
5. 所有金额单位均为人民币（元） 
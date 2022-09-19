var g_health_chart;
var health_chart_option;

function health_chart_initialize(id) {
    // 基于准备好的dom，初始化echarts实例
    g_health_chart = echarts.init(document.getElementById(id));

    health_chart_option = {
        series: [{
            name: 'pie1',
            type: 'pie',
            clockWise: true,
            radius: ['60px', '68px'],
            itemStyle: {
                normal: {
                    label: {
                        show: false
                    },
                    labelLine: {
                        show: false
                    }
                }
            },
            hoverAnimation: false,
            data: [{
                name: 'gap',
                value: 0,
                itemStyle: {
                    normal: {
                        label: {
                            show: false
                        },
                        labelLine: {
                            show: false
                        },
                        color: '#1989FA',
                        borderColor: '#1989FA',
                        
                    }
                }
            }, {
                value: 100,
                name: 'completed',
                itemStyle: {
                    normal: {
                        borderColor: '#D7D8E0',
                        color: '#D7D8E0',
                        label: {
                            show: false
                        },
                        labelLine: {
                            show: false
                        }
                    }
                }
            },]
        }]
    };
    // 使用刚指定的配置项和数据显示图表。
    g_health_chart.setOption(health_chart_option);
}

function health_chart_update(value) {
    // health_chart_option.title.text = `${value}  `
    health_chart_option.series[0].data[0].value = value;
    health_chart_option.series[0].data[1].value = 100 - value;
    g_health_chart.setOption(health_chart_option);
}

var g_tsdb_chart;
var tsdb_chart_option;

function tsdb_chart_initialize(id) {
    // 基于准备好的dom，初始化echarts实例
    g_tsdb_chart = echarts.init(document.getElementById(id));
    // 指定图表的配置项和数据
    var colors = ['#097AF2', '#00B5B5', '#ED9726'];
    tsdb_chart_option = {
        color: colors,
        tooltip: {
            trigger: 'axis',
            axisPointer: {
                type: 'cross'
            },
            backgroundColor: '#fff',
            textStyle: {
                color: '#8A8B99'
            },
            extraCssText: 'box-shadow: 0px 2px 13px 0px rgba(0,0,0,0.16);'
        },
        grid: {
            left: '8%',
            top: '14%',
            width: '88%',
            height: '55%',
        },
        
        dataZoom: [{
            xAxisIndex: [0],
            startValue: '2019-11-21',
            realtime: true,
            height: '30',
            handleStyle: {
                color: '#0A6EFA',
                shadowBlur: 3,
                shadowColor: 'rgba(0, 0, 0, 0.6)',
                shadowOffsetX: 2,
                shadowOffsetY: 2
            },
            dataBackground: {
                areaStyle: {
                    color: 'rgba(10,110,250,0.1)'
                },
                lineStyle: {
                    color: "rgba(10,110,250,1)"
                },
            },
            fillerColor: 'rgba(10,110,250,0.1)'
        },
        {
            xAxisIndex: [0],
            type: 'inside'
        }],
        legend: {
            bottom: '40',
            algin: 'center',
            icon: 'line',
            data: ['Voltage', 'Current', 'Temperature']
        },
        xAxis: [{
            gridIndex: 0,
            type: 'category',
            axisTick: {
                alignWithLabel: true
            },
            axisLine: {
                lineStyle: {
                    color: '#D7D8E0'
                }
            },
            axisTick: {
                show: false
            },
            axisLabel: {
                textStyle: {
                    fontSize: 13,
                    color: '#A4A5B3'
                },
            },
            data: []
        },
        ],
        yAxis: [{
            gridIndex: 0,
            type: 'value',
            name: 'V',
            min: -10,
            max: 40,
            position: 'left',
            offset: 50,
            nameTextStyle: {
                color: "#A4A5B3"
            },
            axisLine: {
                lineStyle: {
                    color: colors[0]
                }
            },
            axisLine: {
                show: false
            },
            axisTick: {
                show: false
            },
            axisLabel: {
                textStyle: {
                    fontSize: 13,
                    color: '#A4A5B3'
                },
                formatter: '{value}'
            },
            splitLine: {
                show: true,
                lineStyle: {
                    type: 'dashed'

                }
            }
        }, {
            gridIndex: 0,
            type: 'value',
            name: 'A',
            min: -10,
            max: 40,
            position: 'left',
            splitLine: { show: false },
            nameTextStyle: {
                color: "#A4A5B3"
            },
            axisLine: {
                lineStyle: {
                    color: colors[1]
                }
            },
            axisLine: {
                show: false
            },
            axisTick: {
                show: false
            },
            axisLabel: {
                textStyle: {
                    fontSize: 13,
                    color: '#A4A5B3'
                },
                formatter: '{value}'
            },
            splitLine: {
                show: true,
                lineStyle: {
                    type: 'dashed'

                }

            },
        }, {
            gridIndex: 0,
            type: 'value',
            name: '℃',
            min: -10,
            max: 90,
            position: 'right',
            splitLine: { show: false },
            nameTextStyle: {
                color: "#A4A5B3"
            },
            axisLine: {
                lineStyle: {
                    color: colors[2]
                }
            },
            axisLabel: {
                textStyle: {
                    fontSize: 13,
                    color: '#A4A5B3'
                },
                formatter: '{value}'
            },
            axisLine: {
                show: false
            },
            axisTick: {
                show: false
            },
            splitLine: {
                show: true,
                lineStyle: {
                    type: 'dashed'
                }

            },
        }
        ],
        series: [
            {
                xAxisIndex: 0,
                yAxisIndex: 0,
                name: 'Voltage',
                type: 'line',
                smooth: true,
                // symbol: 'none',
                tooltip: {
                    trigger: 'axis',
                    axisPointer: {
                        type: 'cross'
                    }
                },
                data: []
            }, {
                xAxisIndex: 0,
                yAxisIndex: 1,
                name: 'Current',
                type: 'line',
                // symbol: 'none',
                smooth: true,
                data: []
            }, {
                xAxisIndex: 0,
                yAxisIndex: 2,
                name: 'Temperature',
                type: 'line',
                // symbol: 'none',
                smooth: true,
                data: []
            }
        ]
    };
    
    // 使用刚指定的配置项和数据显示图表。
    
    g_tsdb_chart.setOption(tsdb_chart_option);   
}

function tsdb_chart_update(time, voltage, current, temp) {

    tsdb_chart_option.xAxis[0].data = time;
    tsdb_chart_option.series[0].data = voltage;
    tsdb_chart_option.series[1].data = current;
    tsdb_chart_option.series[2].data = temp;
    // 使用刚指定的配置项和数据显示图表。
    g_tsdb_chart.setOption(tsdb_chart_option);
}
function window_resize() {
    g_tsdb_chart.resize();
}



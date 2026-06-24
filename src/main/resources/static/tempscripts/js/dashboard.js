// Vertical Bar Chart
var ctx1 = document.getElementById('verticalBarChart');
if(ctx1) {
	var myChart1 = new Chart(ctx1, {
	    type: 'bar',
	    data: {
	        labels: ['January', 'February', 'March', 'April', 'May', 'June', 'July', 'August', 'September', 'October', 'November', 'December'],
	        datasets: [{
	            label: 'Title of Vertical Bar Chart',
	            data: [12, 19, 3, 5, 2, 21, 3, 4, 1, 9, 4, 11],
	            backgroundColor: [
	                'rgba(255, 99, 132, 0.2)',
	                'rgba(54, 162, 235, 0.2)',
	                'rgba(255, 206, 86, 0.2)',
	                'rgba(75, 192, 192, 0.2)',
	                'rgba(153, 102, 255, 0.2)',
	                'rgba(255, 159, 64, 0.2)',
	                'rgba(255, 99, 132, 0.2)',
	                'rgba(54, 162, 235, 0.2)',
	                'rgba(255, 206, 86, 0.2)',
	                'rgba(75, 192, 192, 0.2)',
	                'rgba(153, 102, 255, 0.2)',
	                'rgba(255, 159, 64, 0.2)'
	            ],
	            borderColor: [
	                'rgba(255, 99, 132, 1)',
	                'rgba(54, 162, 235, 1)',
	                'rgba(255, 206, 86, 1)',
	                'rgba(75, 192, 192, 1)',
	                'rgba(153, 102, 255, 1)',
	                'rgba(255, 159, 64, 1)',
	                'rgba(255, 99, 132, 1)',
	                'rgba(54, 162, 235, 1)',
	                'rgba(255, 206, 86, 1)',
	                'rgba(75, 192, 192, 1)',
	                'rgba(153, 102, 255, 1)',
	                'rgba(255, 159, 64, 1)'
	            ],
	            borderWidth: 1
	        }]
	    },
	    options: {
	        scales: {
	            yAxes: [{
	                ticks: {
	                    beginAtZero: true
	                },
	                scaleLabel: {
						display: true,
						labelString: 'No. of units sold',
						fontSize: '16',
					},
	            }],
	            xAxes: [{
	                scaleLabel: {
						display: true,
						labelString: 'Months',
						fontSize: '16',
					}
	            }]
	        },
	    }
	});
} else {

}






// Horizontal Bar Chart
var ctx2 = document.getElementById('horizontalBarChart');
if(ctx2) {
	var myChart2 = new Chart(ctx2, {
	    type: 'horizontalBar',
	    data: {
	        labels: ['January', 'February', 'March', 'April', 'May', 'June'],
	        datasets: [{
	            label: 'Title of Horizontal Bar Chart',
	            data: [-12, 19, 3, -5, 2, 3],
	            backgroundColor: [
	                'rgba(255, 99, 132, 0.2)',
	                'rgba(54, 162, 235, 0.2)',
	                'rgba(255, 206, 86, 0.2)',
	                'rgba(75, 192, 192, 0.2)',
	                'rgba(153, 102, 255, 0.2)',
	                'rgba(255, 159, 64, 0.2)'
	            ],
	            borderColor: [
	                'rgba(255, 99, 132, 1)',
	                'rgba(54, 162, 235, 1)',
	                'rgba(255, 206, 86, 1)',
	                'rgba(75, 192, 192, 1)',
	                'rgba(153, 102, 255, 1)',
	                'rgba(255, 159, 64, 1)'
	            ],
	            borderWidth: 1
	        }]
	    },
	    options: {
	        scales: {
	            xAxes: [{
	                ticks: {
	                    beginAtZero: true
	                },
	                scaleLabel: {
						display: true,
						labelString: 'Percentage change',
						fontSize: '16',
					}
	            }],
	            yAxes: [{
	                scaleLabel: {
						display: true,
						labelString: 'Months',
						fontSize: '16',
					}
	            }]
	        }
	    }
	});
} else {

}






// Vertical Group Bar Chart
var ctx3 = document.getElementById('verticalGroupBarChart');
if(ctx3) {
	var myChart3 = new Chart(ctx3, {
	    type: 'bar',
	    data: {
	        labels: ['January', 'February', 'March', 'April', 'May', 'June', 'July', 'August', 'September', 'October', 'November', 'December'],
	        datasets: [{
	            label: '#2017',
	            data: [12, 19, 3, 5, 2, 21, 3, 4, 1, 9, 4, 11],
	            backgroundColor: 
	                'rgba(255, 99, 132, 0.2)'
	            ,
	            borderColor: 
	                'rgba(255, 99, 132, 1)'
	            ,
	            borderWidth: 1
	        }, {
	            label: '#2018',
	            data: [1, 9, 5, 14, 2, 8, 3, 19, 2, 8, 9, 2],
	            backgroundColor: 
	                'rgba(54, 162, 235, 0.2)'
	            ,
	            borderColor: 
	                'rgba(54, 162, 235, 1)'
	            ,
	            borderWidth: 1
	        }, {
	            label: '#2019',
	            data: [10, 2, 9, 1, 16, 2, 6, 11, 2, 1, 6, 8],
	            backgroundColor: 
	                'rgba(255, 206, 86, 0.2)'
	            ,
	            borderColor: 
	                'rgba(255, 206, 86, 1)'
	            ,
	            borderWidth: 1
	        }]
	    },
	    options: {
	        scales: {
	            yAxes: [{
	                ticks: {
	                    beginAtZero: true
	                },
	                scaleLabel: {
						display: true,
						labelString: 'Production Units (in No.)',
						fontSize: '16',
					}
	            }],
	            xAxes: [{
	                scaleLabel: {
						display: true,
						labelString: 'Months',
						fontSize: '16',
					}
	            }]
	        }
	    }
	});
} else {

}






// Stacked Bar Chart
var ctx4 = document.getElementById('stackedBarChart');
if(ctx4) {
	var myChart4 = new Chart(ctx4, {
	    type: 'bar',
	    data: {
	        labels: ['January', 'February', 'March', 'April', 'May', 'June', 'July'],
	        datasets: [{
	            label: 'Dataset 1',
	            data: [-12, 3, 2, -5, 2, 3, 5],
	            backgroundColor: 
	                'rgba(255,99,132, 1)'
	            ,
	            borderWidth: 1
	        }, {
	            label: 'Dataset 2',
	            data: [-2, 13, 1, 5, 4, 7, 2],
	            backgroundColor: 
	                'rgba(54,162,235, 1)'
	            ,
	            borderWidth: 1
	        }, {
	            label: 'Dataset 3',
	            data: [5, 2, 3, -3, 1, 5, 8],
	            backgroundColor: 
	                'rgba(75,192,192, 1)'
	            ,
	            borderWidth: 1
	        }]
	    },
	    options: {
	        scales: {
	            xAxes: [{
	                stacked: true,
	                scaleLabel: {
						display: true,
						labelString: 'Months',
						fontSize: '16',
					}
	            }],
	            yAxes: [{
	                stacked: true,
	                scaleLabel: {
						display: true,
						labelString: 'No. of Units',
						fontSize: '16',
					}
	            }]
	        }
	    }
	});
} else {

}






// Stacked Group Bar Chart
var ctx5 = document.getElementById('stackedGroupBarChart');
if(ctx5) {
	var myChart5 = new Chart(ctx5, {
	    type: 'bar',
	    data: {
	        labels: ['January', 'February', 'March', 'April', 'May', 'June', 'July'],
	        datasets: [{
	            label: 'Dataset 1',
	            stack: 'Stack 0',
	            data: [-12, 3, 2, -5, 2, 3, 5],
	            backgroundColor: 
	                'rgba(255,99,132, 1)'
	            ,
	            borderWidth: 1
	        }, {
	            label: 'Dataset 2',
	            stack: 'Stack 0',
	            data: [-2, 13, 1, 5, 4, 7, 2],
	            backgroundColor: 
	                'rgba(54,162,235, 1)'
	            ,
	            borderWidth: 1
	        }, {
	            label: 'Dataset 3',
	            stack: 'Stack 1',
	            data: [5, 2, 3, -3, 1, 5, 8],
	            backgroundColor: 
	                'rgba(75,192,192, 1)'
	            ,
	            borderWidth: 1
	        }]
	    },
	    options: {
	        scales: {
	            xAxes: [{
	                stacked: true,
	                scaleLabel: {
						display: true,
						labelString: 'Months',
						fontSize: '16',
					}
	            }],
	            yAxes: [{
	                stacked: true,
	                scaleLabel: {
						display: true,
						labelString: 'No. of Units',
						fontSize: '16',
					}
	            }]
	        }
	    }
	});
} else {

}






// Basic Line Chart
var ctx6 = document.getElementById('basicLineChart');
if(ctx6) {
	var myChart6 = new Chart(ctx6, {
	    type: 'line',
	    data: {
	        labels: ['January', 'February', 'March', 'April', 'May', 'June', 'July'],
	        datasets: [{
	            label: 'Linear DS 1',
	            data: [12, 19, 3, 5, 2, 21, 3],
	            backgroundColor: 'rgba(255,99,132, 1)',
	            borderColor: 'rgba(255,99,132, 1)',
	            borderWidth: 2,
	            fill: false
	        }, {
	            label: 'Linear DS 2',
	            data: [2, 9, 13, 1, 12, 4, 8],
	            backgroundColor: 'rgba(54,162,235, 1)',
	            borderColor: 'rgba(54,162,235, 1)',
	            borderWidth: 2,
	            fill: false
	        }]
	    },
	    options: {
	        scales: {
	            yAxes: [{
	            	scaleLabel: {
						display: true,
						labelString: 'No. of units sold',
						fontSize: '16',
					},
	                ticks: {
	                    beginAtZero: true
	                }
	            }],
	            xAxes: [{
	            	scaleLabel: {
						display: true,
						labelString: 'Months',
						fontSize: '16',
					},
	            }]
	        }
	    }
	});
} else {
}





// Basic Line Chart 2
var ctx6 = document.getElementById('basicLineChart2');
if(ctx6) {
	var myChart6 = new Chart(ctx6, {
	    type: 'line',
	    data: {
	        labels: ['2012', '2013', '2014', '2015', '2016', '2017', '2018', '2019', '2020'],
	        datasets: [{
	            label: 'No. of Students Registered',
	            data: [65982, 72386, 79875, 82567, 89764, 93567, 102273, 136789, 157823],
	            backgroundColor: 'rgba(54,162,235, 1)',
	            borderColor: 'rgba(54,162,235, 1)',
	            borderWidth: 2,
	            fill: false
	        }]
	    },
	    options: {
	        scales: {
	            yAxes: [{
	            	scaleLabel: {
						display: true,
						labelString: 'No. of Students Registered',
						fontSize: '16',
					},
	                
	            }],
	            xAxes: [{
	            	scaleLabel: {
						display: true,
						labelString: 'Years',
						fontSize: '16',
					},
	            }]
	        }
	    }
	});
} else {
}




// Multiaxis Line Chart
var ctx7 = document.getElementById('multiaxisLineChart');
if(ctx7) {
	var myChart7 = new Chart(ctx7, {
	    type: 'line',
	    data: {
	        labels: ['January', 'February', 'March', 'April', 'May', 'June', 'July'],
	        datasets: [{
	            label: 'Linear DS 1',
	            data: [12, 19, 3, 5, 2, 21, 3],
	            backgroundColor: 'rgba(255,99,132, 1)',
	            borderColor: 'rgba(255,99,132, 1)',
	            borderWidth: 2,
	            fill: false,
	            yAxisID: 'y-axis-1',
	        }, {
	            label: 'Logarithmic DS 2',
	            data: [2, 1000, 13, 1, 12, 4, 8],
	            backgroundColor: 'rgba(54,162,235, 1)',
	            borderColor: 'rgba(54,162,235, 1)',
	            borderWidth: 2,
	            fill: false,
	            yAxisID: 'y-axis-2',
	        }]
	    },
	    options: {
	        scales: {
				yAxes: [{
					type: 'linear',
					display: true,
					position: 'left',
					id: 'y-axis-1',
					ticks: {
	                    beginAtZero: true
	                },
	                scaleLabel: {
						display: true,
						labelString: 'No. of units sold in linear scale',
						fontSize: '16',
					}
				}, {
					type: 'logarithmic',
					display: true,
					position: 'right',
					id: 'y-axis-2',

					// grid line settings
					gridLines: {
						drawOnChartArea: false, // only want the grid lines for one axis to show up
					},
					scaleLabel: {
						display: true,
						labelString: 'No. of units sold in log scale',
						fontSize: '16',
					},
				}],
				xAxes: [{
					scaleLabel: {
						display: true,
						labelString: 'Months',
						fontSize: '16',
					},
				}]
			}
	    }
	});
} else {

}






// Stepped Line Chart
var ctx8 = document.getElementById('steppedLineChart');
if(ctx8) {
	var myChart89 = new Chart(ctx8, {
	    type: 'line',
	    data: {
	        labels: ['January', 'February', 'March', 'April', 'May', 'June', 'July'],
	        datasets: [{
	            label: 'Dataset 1',
	            data: [12, 19, 3, 5, 2, 21, 3],
	            backgroundColor: 'rgba(255,99,132, 1)',
	            borderColor: 'rgba(255,99,132, 1)',
	            borderWidth: 2,
	            fill: false,
	            steppedLine: true
	        }, {
	            label: 'Dataset 2',
	            data: [2, 9, 13, 1, 12, 4, 8],
	            backgroundColor: 'rgba(54,162,235, 1)',
	            borderColor: 'rgba(54,162,235, 1)',
	            borderWidth: 2,
	            fill: false,
	            steppedLine: 'middle'
	        }]
	    },
	    options: {
	        scales: {
	            yAxes: [{
	                ticks: {
	                    beginAtZero: true
	                },
	                scaleLabel: {
						display: true,
						labelString: 'No. of units sold',
						fontSize: '16',
					}
	            }],
	            xAxes: [{
	                scaleLabel: {
						display: true,
						labelString: 'Months',
						fontSize: '16',
					}
	            }]
	        }
	    }
	});
} else {

}






// Interpolation Line Chart
var ctx9 = document.getElementById('interpolationLineChart');
if(ctx9) {
	var myChart9 = new Chart(ctx9, {
	    type: 'line',
	    data: {
	        labels: ['January', 'February', 'March', 'April', 'May'],
	        datasets: [{
	            label: 'Dataset 1 (monotone)',
	            data: [2, 9, 13, 1, 12],
	            backgroundColor: 'rgba(255,99,132, 1)',
	            borderColor: 'rgba(255,99,132, 1)',
	            borderWidth: 2,
	            fill: false,
	            cubicInterpolationMode: 'monotone'
	        }, {
	            label: 'Dataset 2 (default)',
	            data: [2, 9, 13, 1, 12],
	            backgroundColor: 'rgba(54,162,2, 1)',
	            borderColor: 'rgba(54,162,2, 1)',
	            borderWidth: 2,
	            fill: false,
	        }, {
	            label: 'Dataset 3 (linear)',
	            data: [2, 9, 13, 1, 12],
	            backgroundColor: 'rgba(54,162,235, 1)',
	            borderColor: 'rgba(54,162,235, 1)',
	            borderWidth: 2,
	            fill: false,
	            lineTension: 0
	        }]
	    },
	    options: {
	        scales: {
	            yAxes: [{
	                ticks: {
	                    beginAtZero: true
	                },
	                scaleLabel: {
						display: true,
						labelString: 'No. of units sold',
						fontSize: '16',
					}
	            }],
	            xAxes: [{
	            	scaleLabel: {
						display: true,
						labelString: 'Months',
						fontSize: '16',
					}
	            }]
	        }
	    }
	});
} else {

}






// Line Styles Chart
var ctx10 = document.getElementById('lineStylesChart');
if(ctx10) {
	var myChart10 = new Chart(ctx10, {
	    type: 'line',
	    data: {
	        labels: ['January', 'February', 'March', 'April', 'May'],
	        datasets: [{
	            label: 'Dataset 1 (unfilled)',
	            data: [2, 9, 13, 1, 12],
	            backgroundColor: 'rgba(255,99,132, 1)',
	            borderColor: 'rgba(255,99,132, 1)',
	            borderWidth: 2,
	            fill: false,
	        }, {
	            label: 'Dataset 2 (dashed)',
	            data: [5, 1, 3, 10, 6],
	            backgroundColor: 'rgba(54,162,2, 1)',
	            borderColor: 'rgba(54,162,2, 1)',
	            borderDash: [5, 5],
	            borderWidth: 2,
	            fill: false,
	        }, {
	            label: 'Dataset 3 (filled)',
	            data: [1, 14, 6, 7, 4],
	            backgroundColor: 'rgba(54,162,235, 0.2)',
	            borderColor: 'rgba(54,162,235, 1)',
	            borderWidth: 2,
	        }]
	    },
	    options: {
	        scales: {
	            yAxes: [{
	                ticks: {
	                    beginAtZero: true
	                },
	                scaleLabel: {
						display: true,
						labelString: 'No. of units sold',
						fontSize: '16',
					}
	            }],
	            xAxes: [{
	            	scaleLabel: {
						display: true,
						labelString: 'Months',
						fontSize: '16',
					}
	            }]
	        }
	    }
	});
} else {

}






// Point Circle Line Chart
var ctx11 = document.getElementById('pointCircleLineChart');
if(ctx11) {
	var myChart11 = new Chart(ctx11, {
	    type: 'line',
	    data: {
	        labels: ['January', 'February', 'March', 'April', 'May', 'June', 'July'],
	        datasets: [{
	            label: 'Point style: Circle',
	            data: [2, 9, 5, 1, 7, 2, 5],
	            backgroundColor: 'rgba(255,99,132, 1)',
	            borderColor: 'rgba(255,99,132, 1)',
	            borderWidth: 2,
	            pointStyle: 'circle',
	            pointRadius: 10,
				pointHoverRadius: 15,
	            fill: false,
	            showLine: false
	        }]
	    },
	    options: {
	        scales: {
	            yAxes: [{
	                ticks: {
	                    beginAtZero: true
	                },
	                scaleLabel: {
						display: true,
						labelString: 'No. of units sold',
						fontSize: '16',
					}
	            }],
	            xAxes: [{
	            	scaleLabel: {
						display: true,
						labelString: 'Months',
						fontSize: '16',
					}
	            }]
	        }
	    }
	});
} else {

}






// Point Triangle Line Chart
var ctx12 = document.getElementById('pointTriangleLineChart');
if(ctx12) {
	var myChart12 = new Chart(ctx12, {
	    type: 'line',
	    data: {
	        labels: ['January', 'February', 'March', 'April', 'May', 'June', 'July'],
	        datasets: [{
	            label: 'Point style: Triangle',
	            data: [1, 3, 2, 5, 2, 9, 0],
	            backgroundColor: 'rgba(255,99,132, 1)',
	            borderColor: 'rgba(255,99,132, 1)',
	            borderWidth: 2,
	            pointStyle: 'triangle',
	            pointRadius: 10,
				pointHoverRadius: 15,
	            fill: false,
	            showLine: false
	        }]
	    },
	    options: {
	        scales: {
	            yAxes: [{
	                ticks: {
	                    beginAtZero: true
	                },
	                scaleLabel: {
						display: true,
						labelString: 'No. of units sold',
						fontSize: '16',
					}
	            }],
	            xAxes: [{
	            	scaleLabel: {
						display: true,
						labelString: 'Months',
						fontSize: '16',
					}
	            }]
	        }
	    }
	});
} else {

}






// Point CrossRot Line Chart
var ctx13 = document.getElementById('pointCrossRotLineChart');
if(ctx13) {
	var myChart13 = new Chart(ctx13, {
	    type: 'line',
	    data: {
	        labels: ['January', 'February', 'March', 'April', 'May', 'June', 'July'],
	        datasets: [{
	            label: 'Point style: Cross',
	            data: [1, 3, 2, 5, 2, 9, 0],
	            backgroundColor: 'rgba(255,99,132, 1)',
	            borderColor: 'rgba(255,99,132, 1)',
	            borderWidth: 2,
	            pointStyle: 'crossRot',
	            pointRadius: 10,
				pointHoverRadius: 15,
	            fill: false,
	            showLine: false
	        }]
	    },
	    options: {
	        scales: {
	            yAxes: [{
	                ticks: {
	                    beginAtZero: true
	                },
	                scaleLabel: {
						display: true,
						labelString: 'No. of units sold',
						fontSize: '16',
					}
	            }],
	            xAxes: [{
	            	scaleLabel: {
						display: true,
						labelString: 'Months',
						fontSize: '16',
					}
	            }]
	        }
	    }
	});
} else {

}






// Point Line Line Chart
var ctx14 = document.getElementById('pointLineLineChart');
if(ctx14) {
	var myChart14 = new Chart(ctx14, {
	    type: 'line',
	    data: {
	        labels: ['January', 'February', 'March', 'April', 'May', 'June', 'July'],
	        datasets: [{
	            label: 'Point style: Line',
	            data: [1, 3, 2, 5, 2, 9, 0],
	            backgroundColor: 'rgba(255,99,132, 1)',
	            borderColor: 'rgba(255,99,132, 1)',
	            borderWidth: 2,
	            pointStyle: 'line',
	            pointRadius: 10,
				pointHoverRadius: 15,
	            fill: false,
	            showLine: false
	        }]
	    },
	    options: {
	        scales: {
	            yAxes: [{
	                ticks: {
	                    beginAtZero: true
	                },
	                scaleLabel: {
						display: true,
						labelString: 'No. of units sold',
						fontSize: '16',
					}
	            }],
	            xAxes: [{
	            	scaleLabel: {
						display: true,
						labelString: 'Months',
						fontSize: '16',
					}
	            }]
	        }
	    }
	});
} else {

}






// Fill False Area Chart
var ctx15 = document.getElementById('fillFalseAreaChart');
if(ctx15) {
	var myChart15 = new Chart(ctx15, {
	    type: 'line',
	    data: {
	        labels: ['January', 'February', 'March', 'April', 'May', 'June', 'July', 'August'],
	        datasets: [{
	            label: 'Area (fill: false)',
	            data: [1, 3, 2, -5, 12, -9, 0, 5],
	            backgroundColor: 'rgba(255,99,132, 1)',
	            borderColor: 'rgba(255,99,132, 1)',
	            borderWidth: 2,
	            pointRadius: 3	,
				pointHoverRadius: 5,
	            fill: false,
	        }]
	    },
	    options: {
	        scales: {
	            yAxes: [{
	                ticks: {
	                    beginAtZero: true
	                },
	                scaleLabel: {
						display: true,
						labelString: 'Profit (in percentage)',
						fontSize: '16',
					}
	            }],
	            xAxes: [{
	            	scaleLabel: {
						display: true,
						labelString: 'Months',
						fontSize: '16',
					}
	            }]
	        }
	    }
	});
} else {

}






// Fill Origin Area Chart
var ctx16 = document.getElementById('fillOriginAreaChart');
if(ctx16) {
	var myChart16 = new Chart(ctx16, {
	    type: 'line',
	    data: {
	        labels: ['January', 'February', 'March', 'April', 'May', 'June', 'July', 'August'],
	        datasets: [{
	            label: 'Area (fill: origin)',
	            data: [1, 3, 2, -5, 12, -9, 0, 5],
	            backgroundColor: 'rgba(255,99,132, 0.3)',
	            borderColor: 'rgba(255,99,132, 1)',
	            borderWidth: 2,
	            pointRadius: 3	,
				pointHoverRadius: 5,
	            fill: 'origin',
	        }]
	    },
	    options: {
	        scales: {
	            yAxes: [{
	                ticks: {
	                    beginAtZero: true
	                },
	                scaleLabel: {
						display: true,
						labelString: 'Profit (in percentage)',
						fontSize: '16',
					}
	            }],
	            xAxes: [{
	            	scaleLabel: {
						display: true,
						labelString: 'Months',
						fontSize: '16',
					}
	            }]
	        }
	    }
	});
} else {

}






// Fill Start Area Chart
var ctx17 = document.getElementById('fillStartAreaChart');
if(ctx17) {
	var myChart17 = new Chart(ctx17, {
	    type: 'line',
	    data: {
	        labels: ['January', 'February', 'March', 'April', 'May', 'June', 'July', 'August'],
	        datasets: [{
	            label: 'Area (fill: start)',
	            data: [1, 3, 2, -5, 12, -9, 0, 5],
	            backgroundColor: 'rgba(255,99,132, 0.3)',
	            borderColor: 'rgba(255,99,132, 1)',
	            borderWidth: 2,
	            pointRadius: 3	,
				pointHoverRadius: 5,
	            fill: 'start',
	        }]
	    },
	    options: {
	        scales: {
	            yAxes: [{
	                ticks: {
	                    beginAtZero: true
	                },
	                scaleLabel: {
						display: true,
						labelString: 'Profit (in percentage)',
						fontSize: '16',
					}
	            }],
	            xAxes: [{
	            	scaleLabel: {
						display: true,
						labelString: 'Months',
						fontSize: '16',
					}
	            }]
	        }
	    }
	});
} else {

}






// Fill End Area Chart
var ctx18 = document.getElementById('fillEndAreaChart');
if(ctx18) {
	var myChart18 = new Chart(ctx18, {
	    type: 'line',
	    data: {
	        labels: ['January', 'February', 'March', 'April', 'May', 'June', 'July', 'August'],
	        datasets: [{
	            label: 'Area (fill: end)',
	            data: [1, 3, 2, -5, 12, -9, 0, 5],
	            backgroundColor: 'rgba(255,99,132, 0.3)',
	            borderColor: 'rgba(255,99,132, 1)',
	            borderWidth: 2,
	            pointRadius: 3	,
				pointHoverRadius: 5,
	            fill: 'end',
	        }]
	    },
	    options: {
	        scales: {
	            yAxes: [{
	                ticks: {
	                    beginAtZero: true
	                },
	                scaleLabel: {
						display: true,
						labelString: 'Profit (in percentage)',
						fontSize: '16',
					}
	            }],
	            xAxes: [{
	            	scaleLabel: {
						display: true,
						labelString: 'Months',
						fontSize: '16',
					}
	            }]
	        }
	    }
	});
} else {

}






// Datasets Area Chart
var ctx19 = document.getElementById('datasetsAreaChart');
if(ctx19) {
	var myChart19 = new Chart(ctx19, {
	    type: 'line',
	    data: {
	        labels: ['January', 'February', 'March', 'April', 'May', 'June', 'July', 'August'],
	        datasets: [{
	            label: '#DS 1',
	            data: [110, 130, 120, 90, 120, 190, 123, 159],
	            backgroundColor: 'rgba(54, 162, 235, 0.2)',
	            borderColor: 'rgba(54, 162, 235, 0)',
	            borderWidth: 2,
	            pointRadius: 3,
				fill: '-1',
	        }, {
	            label: '#DS 2',
	            data: [100, 119, 103, 83, 102, 162, 112, 139],
	            backgroundColor: 'rgba(54, 162, 235, 0.3)',
	            borderColor: 'rgba(54, 162, 235, 0)',
	            borderWidth: 2,
	            pointRadius: 3,
	            fill: '-1',
	        }, {
	            label: '#DS 3',
	            data: [90, 109, 96, 69, 91, 123, 102, 119],
	            backgroundColor: 'rgba(54, 162, 235, 0.4)',
	            borderColor: 'rgba(54, 162, 235, 0)',
	            borderWidth: 2,
	            pointRadius: 3,
	            fill: '-1',
	        }, {
	            label: '#DS 4',
	            data: [80, 99, 76, 59, 87, 103, 97, 109],
	            backgroundColor: 'rgba(54, 162, 235, 0.5)',
	            borderColor: 'rgba(54, 162, 235, 0)',
	            borderWidth: 2,
	            pointRadius: 3,
	            fill: '-1',
	        }, {
	            label: '#DS 5',
	            data: [60, 59, 56, 39, 67, 83, 77, 89],
	            backgroundColor: 'rgba(54, 162, 235, 0.6)',
	            borderColor: 'rgba(54, 162, 235, 0)',
	            borderWidth: 2,
	            pointRadius: 3,
	            fill: '-1',
	        }, {
	            label: '#DS 6',
	            data: [20, 19, 36, 19, 37, 53, 47, 59],
	            backgroundColor: 'rgba(54, 162, 235, 0.7)',
	            borderColor: 'rgba(54, 162, 235, 0)',
	            borderWidth: 2,
	            pointRadius: 3,
	            fill: '-1',
	        }]
	    },
	    options: {
	        scales: {
	            yAxes: [{
	                ticks: {
	                    beginAtZero: true
	                },
	                scaleLabel: {
						display: true,
						labelString: 'No. of Units sold',
						fontSize: '16',
					}
	            }],
	            xAxes:[{
	            	scaleLabel: {
						display: true,
						labelString: 'Months',
						fontSize: '16',
					}
	            }]
	        }
	    }
	});
} else {

}






// Radar Area Chart
var ctx20 = document.getElementById('radarAreaChart');
if(ctx20) {
	var myChart20 = new Chart(ctx20, {
	    type: 'radar',
	    data: {
	        labels: ['January', 'February', 'March', 'April', 'May', 'June', 'July', 'August'],
	        datasets: [{
	            label: '#DS 1',
	            data: [190, 190, 190, 190, 190, 190, 190, 190],
	            backgroundColor: 'rgba(54, 162, 235, 0.8)',
	            borderWidth: 2,
	            pointRadius: 3,
				fill: '1',
	        }, {
	            label: '#DS 2',
	            data: [170, 170, 170, 170, 170, 170, 170, 170],
	            backgroundColor: 'rgba(255, 206, 86, 0.8)',
	            borderWidth: 2,
	            pointRadius: 3,
	            fill: '1',
	        }, {
	            label: '#DS 3',
	            data: [150, 150, 150, 150, 150, 150, 150, 150],
	            backgroundColor: 'rgba(75, 192, 192, 0.8)',
	            borderWidth: 2,
	            pointRadius: 3,
	            fill: '1',
	        }, {
	            label: '#DS 4',
	            data: [130, 130, 130, 130, 130, 130, 130, 130],
	            backgroundColor: 'rgba(153, 102, 255, 0.8)',
	            borderWidth: 2,
	            pointRadius: 3,
	            fill: '1',
	        }, {
	            label: '#DS 5',
	            data: [110, 110, 110, 110, 110, 110, 110, 110],
	            backgroundColor: 'rgba(255, 159, 64, 0.8)',
	            borderWidth: 2,
	            pointRadius: 3,
	            fill: '1',
	        }, {
	            label: '#DS 6',
	            data: [50, 60, 85, 90, 55, 63, 76, 66],
	            backgroundColor: 'rgba(255, 99, 132, 0.8)',
	            borderWidth: 2,
	            pointRadius: 3,
	            fill: '1',
	        }]
	    },
	    options: {
	        scales: {
	            
	        }
	    }
	});
} else {

}






// Scatter Chart
var ctx21 = document.getElementById('scatterChart');
if(ctx21) {
	var myChart21 = new Chart(ctx21, {
	    type: 'scatter',
	    data: {
	        labels: ['January', 'February', 'March', 'April', 'May', 'June', 'July', 'August'],
	        datasets: [{
	            label: 'Dataset 1',
	            data: [{x:2, y:5}, {x:5, y:6}, {x:7, y:1}, {x:9, y:6}, {x:11, y:3}, {x:13, y:12}, {x:6, y:7}, {x:3, y:10}],
	            backgroundColor: 'rgba(54, 162, 255, 0.8)',
	            borderWidth: 2,
	            pointRadius: 5,
	            pointHoverRadius: 8,
				fill: '1',
	        }, {
	            label: 'Dataset 2',
	            data: [{x:0, y:1}, {x:3, y:6}, {x:4, y:17}, {x:8, y:6}, {x:11, y:7}, {x:9, y:2}, {x:5, y:9}, {x:2, y:1}],
	            backgroundColor: 'rgba(255, 99, 132, 0.8)',
	            borderWidth: 2,
	            pointRadius: 5,
	            pointHoverRadius: 8,
				fill: '1',
	        }, {
	            label: 'Dataset 3',
	            data: [{x:1, y:11}, {x:2, y:12}, {x:3, y:0}, {x:5, y:15}, {x:7, y:8}, {x:9, y:19}, {x:10, y:10}, {x:13, y:1}],
	            backgroundColor: 'rgba(69, 255, 112, 0.8)',
	            borderWidth: 2,
	            pointRadius: 5,
	            pointHoverRadius: 8,
				fill: '1',
	        }, {
	            label: 'Dataset 4',
	            data: [{x:0, y:19}, {x:3, y:3}, {x:4, y:12}, {x:7, y:16}, {x:8, y:10}, {x:9, y:18}, {x:11, y:17}, {x:12, y:11}],
	            backgroundColor: 'rgba(255, 213, 13, 0.8)',
	            borderWidth: 2,
	            pointRadius: 5,
	            pointHoverRadius: 8,
				fill: '1',
	        }]
	    },
	    options: {
	        scales: {
	            yAxes: [{
	            	type: 'linear',
					display: true,
	                ticks: {
	                    beginAtZero: true
	                },
	                scaleLabel: {
						display: true,
						labelString: 'Data output',
						fontSize: '16',
					}
	            }],
	            xAxes: [{
	                ticks: {
	                    beginAtZero: true
	                },
	                scaleLabel: {
						display: true,
						labelString: 'Data input',
						fontSize: '16',
					}
	            }]
	        }
	    }
	});
} else {

}






// Doughnut Chart
var ctx22 = document.getElementById('doughnutChart');
if(ctx22) {
	var myChart22 = new Chart(ctx22, {
	    type: 'doughnut',
	    data: {
	        labels: ['January', 'February', 'March', 'April', 'May', 'June', 'July'],
	        datasets: [{
	            label: 'Doughnut',
	            data: [40, 4, 36, 19, 53, 67, 59],
	            backgroundColor: [
	            	'rgba(255,99,132,1)', 
	            	'rgba(54,162,235,1)',
	            	'rgba(75,192,192,1)',
	            	'rgba(255,205,86,1)',
	            	'rgba(255,159,64,1)',
	            	'rgba(255,159,164,1)',
	            	'rgba(255,99,232,1)'
	            ],
	            borderWidth: 2,
	            pointRadius: 3,
				fill: '1',
	        }]
	    },
	    options: {
	        
	    }
	});
} else {

}





// Doughnut Chart
var ctx22 = document.getElementById('doughnutChart2');
if(ctx22) {
	var myChart22 = new Chart(ctx22, {
	    type: 'doughnut',
	    data: {
	        labels: ['Successful candidates', 'Unsuccessful candidates'],
	        datasets: [{
	            label: 'Revenue Sources (in percentage)',
	            data: [6, 94],
	            backgroundColor: [
	            	'rgba(75,192,192,1)', 
	            	'rgba(255,99,132,1)',
	            	
	            ],
	            borderWidth: 2,
	            pointRadius: 3,
				fill: '1',
	        }]
	    },
	    options: {
	        
	    }
	});
} else {

}




// Pie Chart
var ctx23 = document.getElementById('pieChart');
if(ctx23) {
	var myChart23 = new Chart(ctx23, {
	    type: 'pie',
	    data: {
	        labels: ['January', 'February', 'March', 'April', 'May', 'June', 'July'],
	        datasets: [{
	            label: 'Pie',
	            data: [40, 4, 36, 19, 53, 67, 59],
	            backgroundColor: [
	            	'rgba(255,99,132,1)', 
	            	'rgba(54,162,235,1)',
	            	'rgba(75,192,192,1)',
	            	'rgba(255,205,86,1)',
	            	'rgba(255,159,64,1)',
	            	'rgba(255,159,164,1)',
	            	'rgba(255,99,232,1)'
	            ],
	            borderWidth: 2,
	            pointRadius: 3,
				fill: '1',
	        }]
	    },
	    options: {
	        
	    }
	});
} else {

}






// Polar Area Chart
var ctx24 = document.getElementById('polarAreaChart');
if(ctx24) {
	var myChart24 = new Chart(ctx24, {
	    type: 'polarArea',
	    data: {
	        labels: ['January', 'February', 'March', 'April', 'May', 'June'],
	        datasets: [{
	            label: 'Polar Area',
	            data: [40, 9, 36, 19, 53, 67],
	            backgroundColor: [
	            	'rgba(255,99,132,1)', 
	            	'rgba(54,112,235,1)',
	            	'rgba(75,192,192,1)',
	            	'rgba(255,205,86,1)',
	            	'rgba(255,159,64,1)',
	            	'rgba(255,159,164,1)',
	            ],
	        }]
	    },
	    options: {
	        
	    }
	});
} else {

}






// Financial Chart
function generateData() {
	var unit = document.getElementById('unit').value;

	function unitLessThanDay() {
		return unit === 'second' || unit === 'minute' || unit === 'hour';
	}

	function beforeNineThirty(date) {
		return date.hour() < 9 || (date.hour() === 9 && date.minute() < 30);
	}

	// Returns true if outside 9:30am-4pm on a weekday
	function outsideMarketHours(date) {
		if (date.isoWeekday() > 5) {
			return true;
		}
		if (unitLessThanDay() && (beforeNineThirty(date) || date.hour() > 16)) {
			return true;
		}
		return false;
	}

	function randomNumber(min, max) {
		return Math.random() * (max - min) + min;
	}

	function randomBar(date, lastClose) {
		var open = randomNumber(lastClose * 0.95, lastClose * 1.05).toFixed(2);
		var close = randomNumber(open * 0.95, open * 1.05).toFixed(2);
		return {
			t: date.valueOf(),
			y: close
		};
	}

	var date = moment('Jan 01 2017', 'MMM DD YYYY');
	var now = moment();
	var data = [];
	var lessThanDay = unitLessThanDay();
	for (; data.length < 600 && date.isBefore(now); date = date.clone().add(1, unit).startOf(unit)) {
		if (outsideMarketHours(date)) {
			if (!lessThanDay || !beforeNineThirty(date)) {
				date = date.clone().add(date.isoWeekday() >= 5 ? 8 - date.isoWeekday() : 1, 'day');
			}
			if (lessThanDay) {
				date = date.hour(9).minute(30).second(0);
			}
		}
		data.push(randomBar(date, data.length > 0 ? data[data.length - 1].y : 30));
	}

	return data;
}

var ctx25 = document.getElementById('financialChart').getContext('2d');
if(ctx25) {
	ctx25.canvas.width = 1000;
	ctx25.canvas.height = 300;

	var color = Chart.helpers.color;
	var cfg = {
		data: {
			datasets: [{
				label: 'Network Signal Traffic',
				position: 'right',
				backgroundColor: color(window.chartColors.blue).alpha(0.5).rgbString(),
				borderColor: window.chartColors.blue,
				data: generateData(),
				type: 'line',
				pointRadius: 0,
				fill: true,
				lineTension: 0,
				borderWidth: 2,
			}]
		},
		options: {
			animation: {
				duration: 0
			},
			scales: {
				xAxes: [{
					type: 'time',
					distribution: 'series',
					offset: true,
					ticks: {
						major: {
							enabled: true,
							fontStyle: 'bold'
						},
						source: 'data',
						autoSkip: true,
						autoSkipPadding: 75,
						maxRotation: 0,
						sampleSize: 100
					},
					afterBuildTicks: function(scale, ticks) {
						var majorUnit = scale._majorUnit;
						var firstTick = ticks[0];
						var i, ilen, val, tick, currMajor, lastMajor;

						val = moment(ticks[0].value);
						if ((majorUnit === 'minute' && val.second() === 0)
								|| (majorUnit === 'hour' && val.minute() === 0)
								|| (majorUnit === 'day' && val.hour() === 9)
								|| (majorUnit === 'month' && val.date() <= 3 && val.isoWeekday() === 1)
								|| (majorUnit === 'year' && val.month() === 0)) {
							firstTick.major = true;
						} else {
							firstTick.major = false;
						}
						lastMajor = val.get(majorUnit);

						for (i = 1, ilen = ticks.length; i < ilen; i++) {
							tick = ticks[i];
							val = moment(tick.value);
							currMajor = val.get(majorUnit);
							tick.major = currMajor !== lastMajor;
							lastMajor = currMajor;
						}
						return ticks;
					},
					scaleLabel: {
						display: true,
						labelString: 'Time / Duration',
						fontSize: '16',
					}
				}],
				yAxes: [{
					gridLines: {
						drawBorder: false
					},
					scaleLabel: {
						display: true,
						labelString: 'MB / sec',
						fontSize: '16',
					}
				}]
			},
			tooltips: {
				intersect: false,
				mode: 'index',
				callbacks: {
					label: function(tooltipItem, myData) {
						var label = myData.datasets[tooltipItem.datasetIndex].label || '';
						if (label) {
							label += ': ';
						}
						label += parseFloat(tooltipItem.value).toFixed(2);
						return label;
					}
				}
			}
		}
	};

	var chart = new Chart(ctx25, cfg);

	document.getElementById('update').addEventListener('click', function() {
		var type = document.getElementById('type').value;
		var dataset = chart.config.data.datasets[0];
		dataset.type = type;
		dataset.data = generateData();
		chart.update();
	});
} else {

}

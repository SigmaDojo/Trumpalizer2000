function getData(callback) {
	d3.json("http://10.13.3.67:3000/data", callback);
	/*
	callback({
		type: "barchart",
		data: [{x: 6, y: 4}, {x: 1, y: 8}, {x: 2, y: 15}, {x: 3, y: 16}, {x: 4, y: 23}, {x: 5, y: 42}]
	})
	*/
}

const colors = [
	"cyan",
	"lime",
	"red",
	"pink",
	"purple",
	"silver",
	"blue"
]

function getColor(n) {
	return colors[n % colors.length]
}

function barchart(list) {
	const max_value = list
	.map(d => d.y)
	.reduce( function(max, d) {
		return (d > max) ? d : max;
	}, 0);

	const width = 400;
	const height = 400;
	const padding = 1;
	const scale = 90;

	const xScale = d3.scaleLinear()
		.domain([0, d3.max(list, function(d) { return d.x; })])
		.range([0, width]);

	const yScale = d3.scaleLinear()
		.domain([0, d3.max(list, function(d) { return d.y; })])
		.range([0, height]);


	//Create SVG element
	var svg = d3.select("#diagram")
		.append("svg")
		.attr("viewBox", `0 0 ${width} ${height}`)
		//.attr("width", width)
		//.attr("height", height);

	svg.selectAll("rect")
		.data(list)
		.enter()
		.append("rect")
		.attr("x", function(d, i) {
			return i * (width / list.length);
		})
		.attr("y", d => height - yScale(d.y))
		.attr("width", width / list.length - padding)
		.attr("height", d => yScale(d.y) - 30)
		.attr("fill", "teal");

	svg.selectAll("text.value")
		.data(list)
		.enter()
		.append("text")
		.text(function(d) {
			return (d.y != 0) ? d.y: "";
		})
		.attr("text-anchor", "middle")

		.attr("font-family", "sans-serif")
		.attr("font-size", "11px")

		.attr("x", function(d, i) {
			return i * (width / list.length) + (width / list.length - padding) / 2;
		})
		.attr("y", function(d) {
			console.log((yScale(d.y) < 24) ? 24 : 0);
			return height - yScale(d.y) + ((yScale(d.y) > 50) ? 24 : -5);
		});


	svg.selectAll("text.label")
		.data(list)
		.enter()
		.append("text")
		.text(function(d) {
			if (typeof(d.label) == 'undefined') {
				return d.x;
			}
			return d.label;
		})

		.attr("text-anchor", "middle")
		.attr("font-family", "sans-serif")
		.attr("font-size", "8px")

		.attr("x", function(d, i) {
			return i * (width / list.length) + (width / list.length - padding) / 2;
		})
		.attr("y", function(d) {
			return height - 10;
		});


/*
	d3.select("#diagram")
		.attr("class", "barchart")
		.selectAll("p")
		.data(list)
		.enter().append("div")
		.attr("class", "bar vertical")
		.style("background-color", function(d, i) {return getColor(i)})
		.style("height", function(d) {
			return (yScale(d.y)/max_value)*75 + "%";
		})
		.text(d => yScale(d.y));
		*/
}

function main() {
	getData( function (res) {
		res.data.sort((a, b) => a.x - b.x);
		barchart(res.data);
	});

}

main();

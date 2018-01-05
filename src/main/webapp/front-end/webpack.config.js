var path = require('path');
const ExtractTextPlugin = require('extract-text-webpack-plugin');
const mkdirp = require('mkdirp');
const copydir = require('copy-dir');

const extractLess = new ExtractTextPlugin({
  filename: '[name].css'
});
// fix TW Bootstrap Issue
mkdirp.sync('./node_modules/bootstrap/less/fonts/', () => console.log(arguments[0]))
copydir.sync(
  './node_modules/transferwise-iconfont/fonts/',
  './node_modules/bootstrap/less/fonts/'
);

module.exports = {
  devtool: 'eval',
  entry: [
    './src/index.jsx',
    './src/styles/app.less'
  ],
  module: {
    rules: [{
      test: /.jsx?$/,
      exclude: /node_modules/,
      loader: 'babel-loader',
      query: {
        presets: ['es2015', 'react']
      }
    }, {
      test: /\.less$/,
      use: extractLess.extract({
        use: [{
          loader: 'css-loader'
        }, {
          loader: 'less-loader'
        }],
        // use style-loader in development
        fallback: 'style-loader'
      })
    },
    {
      test: /\.(eot|svg|ttf|woff|woff2)$/,
      use: [{ loader: 'file-loader?name=./fonts/[name].[ext]'}]
    }]
  },
  plugins: [
    extractLess
  ],
  output: {
    path: path.join(__dirname, 'dist'),
    publicPath: '/front-end/dist/',
    filename: 'bundle.js'
  },
  devServer: {
    contentBase: './dist'
  }
};

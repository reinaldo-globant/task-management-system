module.exports = {
  default: {
    paths: ['features/**/*.feature'],
    require: ['features/step_definitions/**/*.js'],
    format: ['@cucumber/pretty-formatter'],
    publishQuiet: true
  }
};
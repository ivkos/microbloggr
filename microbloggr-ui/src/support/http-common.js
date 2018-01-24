import axios from 'axios';
import AppState from "./AppState";

const HTTP = axios.create({
  baseURL: `http://localhost:8000`,
  responseType: 'json'
});

HTTP.interceptors.request.use(function (config) {
  if (AppState.sessionId) {
    config.headers['X-Session-Id'] = AppState.sessionId;
  }

  return config;
});

export { HTTP };

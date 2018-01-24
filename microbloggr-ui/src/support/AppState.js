const AppState = {
  get sessionId() {
    return this.get('sessionId');
  },

  set sessionId(val) {
    this.set('sessionId', val);
  },

  get userId() {
    return this.get('userId');
  },

  set userId(val) {
    this.set('userId', val);
  },

  get (prop) {
    return localStorage.getItem(prop) || undefined;
  },

  set (prop, val) {
    localStorage.setItem(prop, val);
  },

  clear() {
    localStorage.clear();
  }
};

export default AppState;

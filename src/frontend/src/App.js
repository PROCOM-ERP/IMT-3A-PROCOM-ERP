import './css/App.css';
import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import Authentification from './pages/Authentification.js';
import Home from './pages/Home.js';
import Profil from './pages/Profil.js';

function App() {
  return (
    <div>
      <Router>
        <Routes>
          <Route path='/' exact Component={Authentification} />
          <Route path='/home' exact Component={Home} />
          <Route path='/profil' exact Component={Profil} />
        </Routes>
      </Router>
    </div>
  );
}

export default App;

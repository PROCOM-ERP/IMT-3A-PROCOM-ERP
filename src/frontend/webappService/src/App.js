import "./css/App.css";
import React from "react";
import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import Authentication from "./pages/Authentication.js";

import Home from "./pages/Home.js";
import Directory from "./pages/Directory.js";
import DirectoryProfil from "./pages/DirectoryProfil.js";
import Inventory from "./pages/Inventory.js";
import OrderManagement from "./pages/OrderManagement.js";
import AddOrder from "./pages/AddOrder.js";
import Order from "./pages/Order.js";
import Profil from "./pages/Profil.js";
import ModifyProfil from "./pages/ModifyProfil.js";
import ModifyPassword from "./pages/ModifyPassword.js";

import AdminHome from "./pages/admin/AdminHome.js";
import AdminDirectory from "./pages/admin/AdminDirectory.js";
import AdminUser from "./pages/AdminUser.js";
import AddUser from "./pages/AddUser.js";
import AdminPermissions from "./pages/AdminPermissions.js";
import AddRole from "./pages/AddRole.js";

import Error404 from "./pages/Error404.js";
import Error401 from "./pages/Error401.js";
import Error403 from "./pages/Error403.js";


function App() {
  return (
    <>
      <Router>
        <Routes>
          <Route path="/" exact Component={Authentication} />

          <Route path="/home" exact Component={Home} />
          <Route path="/directory" exact Component={Directory} />
          <Route path="/user/:userId" exact Component={DirectoryProfil} />
          <Route path="/inventory" exact Component={Inventory} />
          <Route path="/orderManagement" exact Component={OrderManagement} />
          <Route path="/addOrder" exact Component={AddOrder} />
          <Route path="/order/:orderId" exact Component={Order} />
          <Route path="/profil/" exact Component={Profil} />
          <Route path="/modifyProfil" exact Component={ModifyProfil} />
          <Route path="/updatePassword" exact Component={ModifyPassword} />

          <Route path="/admin" exact Component={AdminHome} />
          <Route path="/adminDirectory" exact Component={AdminDirectory} />
          <Route path="/manageUser/:userId" exact Component={AdminUser} />
          <Route path="/addUser" exact Component={AddUser} />
          <Route path="/adminPermissions" exact Component={AdminPermissions} />
          <Route path="/addRole" exact Component={AddRole} />

          <Route path="/error401" exact Component={Error401} />
          <Route path="/error403" exact Component={Error403} />
          <Route path="*" exact Component={Error404} />
        </Routes>
      </Router>
    </>
  );
}

export default App;

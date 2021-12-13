// Import the functions you need from the SDKs you need
import { initializeApp } from "https://www.gstatic.com/firebasejs/9.5.0/firebase-app.js";
import { getAnalytics } from "https://www.gstatic.com/firebasejs/9.5.0/firebase-analytics.js";
import { collection, doc, addDoc, getDocs, getFirestore, arrayUnion, updateDoc, deleteDoc } from "https://www.gstatic.com/firebasejs/9.5.0/firebase-firestore.js"; 
// TODO: Add SDKs for Firebase products that you want to use
// https://firebase.google.com/docs/web/setup#available-libraries

// Your web app's Firebase configuration
// For Firebase JS SDK v7.20.0 and later, measurementId is optional
const firebaseConfig = {
  apiKey: "AIzaSyBuThHyBUITXuPUzzisrxX0dluajNVUIgQ",
  authDomain: "refreshing-mark-330722.firebaseapp.com",
  projectId: "refreshing-mark-330722",
  storageBucket: "refreshing-mark-330722.appspot.com",
  messagingSenderId: "340352891149",
  appId: "1:340352891149:web:d45554e02b283810d51844",
  measurementId: "G-R123RWB560"
};

// Initialize Firebase
const app = initializeApp(firebaseConfig);
const analytics = getAnalytics(app);
const db = getFirestore(app);

function accept(comment, id) {

  // let ptest = document.getElementById("test");
  // ptest.innerText = this.getAttribute('class');

  ptest.innerText = id;
  const resRef = doc(db, 'resources', this.getAttribute('class'));
  updateDoc(resRef, {
    comments: arrayUnion(comment)
  });
  const comRef = doc(db, 'comment suggestions', id);
  deleteDoc(comRef);

  // const idTag = "#" + id;
  // $(idTag).fadeOut(2000);
  document.getElementById(id).remove();
}

function decline(id) {
  const comRef = doc(db, 'comment suggestions', id);
  deleteDoc(comRef);

  // const idTag = "#" + id;
  // $(idTag).fadeOut(2000);
  document.getElementById(id).remove();
}

function resolve(id) {
  const issueRef = doc(db, 'resource issues', id);
  deleteDoc(issueRef);

  document.getElementById(id).remove();
}

function deleteRes(id) {
  const resRef = doc(db, 'resources', this.getAttribute('class'));
  deleteDoc(resRef);

  const issueRef = doc(db, 'resource issues', id);
  deleteDoc(issueRef);

  document.getElementById(id).remove();
}

const comment_list = document.querySelector("#comment_body");
const comQuerySnapshot = await getDocs(collection(db, "comment suggestions"));
comQuerySnapshot.forEach((doc) => {
        let tr = document.createElement('tr');
        tr.setAttribute("class", "body_row");
        tr.setAttribute("id", doc.id);

        let address_td = document.createElement('td');
        let type_td = document.createElement('td');

        let comment_td = document.createElement('td');
        comment_td.setAttribute("class", "comment_cell");
        comment_td.setAttribute("id", doc.data().r_id);

        let date_td = document.createElement('td');
        let accept_td = document.createElement('td');
        let decline_td = document.createElement('td');

        let accept_btn = document.createElement('input');
        accept_btn.type = "button";
        accept_btn.value="Accept";
        accept_btn.setAttribute("class", doc.data().r_id);
        accept_btn.onclick=accept.bind(accept_btn, doc.data().comment, doc.id);

        let decline_btn = document.createElement('input');
        decline_btn.setAttribute("class", "cell_btn");
        decline_btn.type = "button";
        decline_btn.value= "Decline";
        decline_btn.onclick=decline.bind(decline_btn, doc.id);

        accept_btn.setAttribute("class", "cell_btn");

        address_td.textContent = doc.data().address;
        type_td.textContent = doc.data().type;
        comment_td.textContent = doc.data().comment;
        date_td.textContent = doc.data().time.toDate().toDateString();
        accept_td.appendChild(accept_btn);
        decline_td.appendChild(decline_btn);

        tr.appendChild(address_td);
        tr.appendChild(type_td);
        tr.appendChild(comment_td);
        tr.appendChild(date_td);
        tr.appendChild(accept_td);
        tr.appendChild(decline_td);

        comment_list.appendChild(tr);
    });

const problem_list = document.querySelector("#problem_body");
const probQuerySnapshot = await getDocs(collection(db, "resource issues"));
probQuerySnapshot.forEach((doc) => {
        let tr = document.createElement('tr');
        tr.setAttribute("class", "body_row");
        tr.setAttribute("id", doc.id);

        let address_td = document.createElement('td');
        let type_td = document.createElement('td');

        let problem_td = document.createElement('td');
        problem_td.setAttribute("class", "comment_cell");
        let date_td = document.createElement('td');
        let resolve_td = document.createElement('td');
        // here
        // resolve_td.setAttribute("text-align", "left");

        let delete_td = document.createElement('td');
        // here
        // delete_td.setAttribute("text-align", "left");

        let resolve_btn = document.createElement('input');
        resolve_btn.setAttribute("class", "cell_btn");
        resolve_btn.type = "button";
        resolve_btn.value="Resolve";
        resolve_btn.onclick=resolve.bind(resolve_btn, doc.id);

        let delete_btn = document.createElement('input');
        delete_btn.setAttribute("class", "cell_btn");
        delete_btn.type = "button";
        delete_btn.value= "Delete Resource";
        delete_btn.setAttribute("class", doc.data().r_id);
        delete_btn.onclick=deleteRes.bind(delete_btn, doc.id);

        address_td.textContent = doc.data().address;
        type_td.textContent = doc.data().type;
        problem_td.textContent = doc.data().issue;
        date_td.textContent = doc.data().time.toDate().toDateString();
        resolve_td.appendChild(resolve_btn);
        delete_td.appendChild(delete_btn);

        tr.appendChild(address_td);
        tr.appendChild(type_td);
        tr.appendChild(problem_td);
        tr.appendChild(date_td);
        tr.appendChild(resolve_td);
        tr.appendChild(delete_td);

        problem_list.appendChild(tr);
    });

const logoutButton = document.getElementById("logout");
logoutButton.addEventListener("click", (e) => {
    e.preventDefault();
    window.location.href="index.html";
});

const submitButton = document.getElementById("submit");
submitButton.addEventListener("click", (e) => {
    e.preventDefault();
    let name = document.getElementById("name");
    let address = document.getElementById("address");
    let latitude = document.getElementById("latitude");
    let longitude = document.getElementById("longitude");
    let type = document.getElementById("type");

    if (name.value.length!=0 && address.value.length!=0 && latitude.value.length!=0 && longitude.value.length!=0 && type.value.length!=0) {
        addDoc(collection(db, "resources"), {
        name: name.value,
        address: address.value,
        latitude: parseFloat(latitude.value),
        longitude: parseFloat(longitude.value),
        type: type.value,
        comments: []
      });

      name.value="";
      address.value="";
      latitude.value="";
      longitude.value="";
      type.value="";
    }
});
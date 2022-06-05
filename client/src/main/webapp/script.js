const savePrivkey = (privkey,email) => {
  localStorage.setItem(email, privkey)
  window.location.replace('http://localhost:8080/security/home.jsp')
}

const getPrivkey = (email) => {
  const hiddenFields = document.querySelectorAll(".privkey")
  hiddenFields.forEach(field => field.value = localStorage.getItem(email))
  return true
}

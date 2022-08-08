<template lang="html">
  <div class="remove-items-compartilhados row" v-if="this.$root.credentials">
    <div class="col-md-10 col-md-offset-1 text-left">
      <h2 class="form-title">Aceitação de compartilhamento</h2>
      <h6 class="form-subtitle">Deseja aceitar o compartilhamento?</h6>

      <div class="success" v-if="success">
        O compartilhamento foi aceito.
      </div>

      <div class="error" v-if="error">
        Ocorreu um erro ao aceitar o compartilhamento.
      </div>

      <div>
        <p class="label"><label>Item compartilhado</label></p>
        <p class="text" >{{compartilhamento.itemNome}}</p>

        <p class="label"><label>Dono do Item</label></p>
        <p class="text" >{{compartilhamento.nomeUsuario}}</p>

        <p class="label"><label>Data de Início</label></p>
        <p class="text" >{{compartilhamento.dataInicio}}</p>

        <p class="label"><label>Data de Término</label></p>
        <p class="text" >{{compartilhamento.dataTermino}}</p>

        <button type="submit" class="btn btn-danger" style="background-color: Green;" @click="aceita()">Sim, aceitar o compartilhamento</button>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  props: ['compartilhamento'],

  data() {
    return {
      error: false,
      success: false,
      aceitar: {
        id: this.compartilhamento.id,
        aceita: true
      },

      httpOptions: {
          baseURL: this.$root.config.url,
          headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json',
            'Authorization': 'Bearer ' + this.$root.credentials.token
          }
      },
    }
  },

  methods: {
    aceita: function() {
      axios.post("/api/compartilhamento/aceita",this.aceitar, this.httpOptions)
        .then(response => {
          this.success = true;
          this.error = false;
          setTimeout(this.goBackToList, 3000);
        })
        .catch(error => {
          this.error = true;
          this.success = false;
        });
    },

    goBackToList: function() {
      this.$router.replace('/compartilhamento/lista');
    }
  }
}
</script>

<style lang="css" scoped>
div.remove-items-compartilhados {
  margin-top: 32px;
}
div.success {
  border: 1px solid green;
  background: lightgreen;
  padding: 8px;
  margin-bottom: 24px;
}
div.error {
  border: 1px solid red;
  background: lightcoral;
  padding: 8px;
  margin-bottom: 24px;
}
p.label {
  color: black;
  font-weight: bold;
  text-align: left;
  display: block;
  font-size: 100%;
  padding: 0px 0px 0px 0px;
  margin-bottom: 4px;
}
p.text {
  margin-bottom: 32px;
}
</style>
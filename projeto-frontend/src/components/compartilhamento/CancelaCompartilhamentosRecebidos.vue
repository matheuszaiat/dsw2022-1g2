<template lang="html">
  <div class="remove-items-compartilhados row" v-if="this.$root.credentials">
    <div class="col-md-10 col-md-offset-1 text-left">
      <h2 class="form-title">Cancelamento de compartilhamento</h2>
      <h6 class="form-subtitle">Deseja cancelar o compartilhamento?</h6>

      <div class="success" v-if="success">
        O compartilhamento foi cancelado.
      </div>

      <div class="error" v-if="error">
        Ocorreu um erro ao cancelar o compartilhamento.
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

        <button type="submit" class="btn btn-danger" @click="cancela()">Sim, cancelar o compartilhamento</button>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  props: ['compartilhamento', 'lista'],

  data() {
    return {
      error: false,
      success: false,
      item: this.compartilhamento,

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
    cancela: function() {
      axios.delete("/api/compartilhamento/" + this.compartilhamento.id, this.httpOptions)
        .then(response => {
          this.success = true;
          this.error = false;
          if(this.lista == true){
            setTimeout(this.goBackToCompartilhamentos, 3000);
          }else{
            setTimeout(this.goBackToList, 3000);
          }
        })
        .catch(error => {
          this.error = true;
          this.success = false;
        });
    },

    goBackToCompartilhamentos: function() {
      this.$router.replace('/compartilhamento/lista');
    },
    goBackToList: function() {
      this.$router.replace('/item/list');
    },
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
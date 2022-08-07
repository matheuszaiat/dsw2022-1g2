<template lang="html">
  <div class="bloco-descricao">
    <div class="item-descricao"></div>
    <h1>Descrição do Item</h1>
    <ul>
      <li>Nome: {{ item.nome }}</li>
      <li>Descrição: {{ item.descricao }}</li>
      <li>Tipo do item: {{ item.tipo }}</li>
      <!-- <div v-for= uso in compartilhamentos >
        <div class="usos-item">
          <h3>Usos do item</h3>
          <li>data de início: {{ uso.data_inicio }}</li>
          <li>data de término: {{ uso.data_termino }}</li>
          <li>nome do usuário que receberá o item: {{ uso.nome_usuario }}</li>
          <li>Status do compartilhamento: {{ uso.status }}</li>
        </div>
      </div>-->
      <div class="usos-item">
        <h3>Usos do item</h3>
        <li>data de início:</li>
        <li>data de término:</li>
        <li>nome do usuário que receberá o item:</li>
        <li>Status do compartilhamento:</li>
        <button @click="compartilhador()">Compartilhar</button>
        <button >Cancelar compartilhamento</button>
  
        <div v-show="this.compartilhar">
          <form @submit.prevent="postarCompartilhamento">
            <div class="form-group">
              <label >Email do usuário</label>
              <input type="text" class="form-control" placeholder="Entre o email do usuário que receberá o item" v-model="form.email">
              <span class="error" ></span>
            </div>

            <div class="form-group">
              <div>
                <label for="descricao">Data de início</label>
                <input type="datetime-local" v-model="form.data_inicio">
              </div>
              <div>
                <label for="descricao">Data de término</label>
                <input type="datetime-local" v-model="form.data_termino">
              </div>
            </div>

            <button type="submit" class="btn btn-primary">Envia</button>
          </form>
        </div>
      </div>
    </ul>
    
     
  </div>

  
</template>
<script>
  import axios from 'axios';
  
  export default {
    props: ['item'],
    
    data() {
      return {
        compartilhar: false,
        form: {
          email: "",
          item: this.item.nome,
          data_inicio: "",
          data_termino: ""
        },
        compartilhamentos: {
        },
        error: {
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

    goBackToList: function() {
      this.$router.replace('/item/list');
    },
    compartilhador: function() {
      this.compartilhar = !this.compartilhar;
    },
    postarCompartilhamento: function(){
      axios.post("/api/compartilhamento/novo", this.form, this.httpOptions)
        .then(response => {
          //this.success = true;
          this.error = {};
          //setTimeout(this.goBackToList, 3000);
        })
        .catch(error => {
          this.error = error.response.data.errors;
        });
    },
    getCompartilhamentos: function(){
      axios.get("/api/compartilhamento/" + this.item.id, this.httpOptions)
        .then(response => {
          //this.success = true;
          this.compartilhamentos = response.data.data.data;
          this.error = {};
          //setTimeout(this.goBackToList, 3000);
        })
        .catch(error => {
          this.error = error.response.data.errors;
        });
    },
    removerCompartilhamentos: function(compartilhamento){
      axios.get("/api/compartilhamento/" + this.compartilhamento.id, this.httpOptions)
        .then(response => {
          //this.success = true;
          this.error = {};
          //setTimeout(this.goBackToList, 3000);
        })
        .catch(error => {
          this.error = error.response.data.errors;
        });
    }

  }
}
</script>
<style>

.bloco-descricao{
    margin-left: auto;
    margin-right: auto;
    display: block;
    width: 500px;
    height: 500px;
    background-color: gray;
    border-radius: 2px;
}

.usos-item{
  margin-top: 20px;
}
</style>
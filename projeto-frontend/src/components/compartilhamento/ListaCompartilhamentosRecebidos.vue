<template lang="html">
  <div class="lista-items-compartilhados row" v-if="this.$root.credentials">
    <div class="col-md-10 col-md-offset-1 text-left">
      <div>
        <div class="header">
          <h2 class="form-title">Compartilhamentos</h2>
          <h6 class="form-subtitle">Abaixo estão os seus compartilhamentos recebidos.</h6>
        </div>
        <div class="clear"></div>
      </div>

      <table class="table table-striped" id="tbItens">
      <thead>
        <tr>
          <th>Nome</th>
          <th>Usuário</th>
          <th>Data de Início</th>
          <th>Data de Término</th>
          <th>Status</th>
          <th class="commands"></th>
        </tr>
      </thead>

      <tbody>
        <tr v-for="compartilhamento in this.compartilhamentos">
          <td>{{compartilhamento.itemNome}}</td>
          <td>{{compartilhamento.nomeUsuario}}</td>
          <td>{{compartilhamento.dataInicio}}</td>
          <td>{{compartilhamento.dataTermino}}</td>
          <td>{{compartilhamento.status}}</td>
          <td style="display:inline;">
            <button type="button" class="btn btn-primary" style="background-color:Green;" @click="aceita(compartilhamento)">Aceitar</button>
            <button type="button" class="btn btn-primary" style="background-color:Red;" @click="rejeita(compartilhamento)">Rejeitar</button>
            <button type="button" class="btn btn-primary" @click="cancela(compartilhamento)">Cancelar</button>
          </td>
        </tr>
      </tbody>
      </table>

      <div>
        <div class="page-item first" :class="{ disable: this.page == 1 }" @click="moveTo(page-1)">&lt;&lt;</div>
        <div class="page-item" v-show="page > 3" @click="moveTo(page-3)">{{page-3}}</div>
        <div class="page-item" v-show="page > 2" @click="moveTo(page-2)">{{page-2}}</div>
        <div class="page-item" v-show="page > 1" @click="moveTo(page-1)">{{page-1}}</div>
        <div class="page-item current disable">{{page}}</div>
        <div class="page-item" v-show="totalPages > page"   @click="moveTo(page+1)">{{page+1}}</div>
        <div class="page-item" v-show="totalPages > page+1" @click="moveTo(page+2)">{{page+2}}</div>
        <div class="page-item" v-show="totalPages > page+2" @click="moveTo(page+3)">{{page+3}}</div>
        <div class="page-item last" :class="{ disable: this.page == this.totalPages }" @click="moveTo(page+1)">&gt;&gt;</div>
        <div class="clear"></div>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  data() {
    return {
      page: 1,
      compartilhamentos: {},
      totalPages: 1,
      items: [],
      filtro: "",

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

  created: function () {
    this.getCompartilhamentos();
  },  

  methods: {
     
     getCompartilhamentos: function(){
      axios.get("/api/compartilhamento/lista/?page=" + this.page + "&sort=&per_page=10", this.httpOptions)
        .then(response => {
          //this.success = true;
          this.page = response.data.data.current_page;
          this.compartilhamentos = response.data.data.data;
          console.log(this.compartilhamentos);
          this.error = {};
          
          //setTimeout(this.goBackToList, 3000);
        })
        .catch(error => {
          this.error = error.response.data.errors;
        });
    },
    /*
    processForm: function() {
      axios.get("/api/item/lista?sort=&per_page=10&page=" + this.page, this.httpOptions)
        .then(response => {
          this.items = response.data.data.data;
          this.page = response.data.data.current_page;
          this.totalPages = response.data.data.last_page;
          this.error = {};
        })
        .catch(error => {
          this.error = error.response.data.errors;
        });
    },*/

    moveTo: function(page) {
      if (page < 1) 
        page = 1;

      if (page > this.totalPages) 
        page = this.totalPages;

      this.page = page;
      this.processForm();
    },

    aceita: function(compartilhamento) {
      this.$router.push({ name: 'aceita-compartilhamento',  params: {compartilhamento: compartilhamento}});
    },

    rejeita: function(compartilhamento) {
      this.$router.push({ name: 'rejeita-compartilhamento', params: {compartilhamento: compartilhamento}});
    },

    cancela: function(compartilhamento) {
      this.$router.push({ name: 'cancela-compartilhamento', params: {compartilhamento: compartilhamento}});
    }
  }
}
</script>

<style lang="css" scoped>
div.lista-items-compartilhados {
  margin-top: 32px;
}
th.commands {
  width: 48px;
}
div.page-item {
  color: #2973b7;
  text-decoration: none;
  cursor: pointer;
  padding: 10px;
  border: 1px solid lightgray;
  margin: 0px -1px 0px 0px;
  float: left;
}
div.page-item.first {
  border-top-left-radius: 8px;
  border-bottom-left-radius: 8px;
}
div.page-item.last {
  border-top-right-radius: 8px;
  border-bottom-right-radius: 8px;
}
div.page-item.disable {
  color: gray;
  cursor: auto;
}
div.page-item.current {
  background-color: lightgray;
}
div.clear {
  clear: both;
}
div.header {
  float: left;
}
div.new-button {
  float: right;
  text-align: right;
}
</style>